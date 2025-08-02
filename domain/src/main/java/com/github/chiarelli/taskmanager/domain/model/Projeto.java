package com.github.chiarelli.taskmanager.domain.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.chiarelli.taskmanager.domain.dto.AlterarProjeto;
import com.github.chiarelli.taskmanager.domain.dto.CriarProjeto;
import com.github.chiarelli.taskmanager.domain.dto.CriarTarefa;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.NovaTarefaCriadaEvent;
import com.github.chiarelli.taskmanager.domain.event.ProjetoAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.ProjetoCriadoEvent;
import com.github.chiarelli.taskmanager.domain.event.ProjetoExcluidoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.exception.CommandAlreadyProcessedException;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.exception.OptimisticLockingFailureException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Projeto extends BaseModel implements iDefaultAggregate {

  @EqualsAndHashCode.Include
  private ProjetoId id;

  private String titulo;
  private String descricao;

  private Long version = 0L;

  private Set<Tarefa> tarefas = new HashSet<>();

  public Projeto(String titulo, String descricao) {
    this.titulo = titulo;
    this.descricao = descricao;
  }

    public Set<Tarefa> getTarefas() {
    return Collections.unmodifiableSet(tarefas);
  }

  @Override
  public Long getVersion() {
    return version;
  }

  @Override
  public String getIdAsString() {
    return id.toString();
  }

  /****** Métodos de negócio de Projeto ******/

  public static Projeto criarNovoProjeto(CriarProjeto data) {
    var projeto = new Projeto(data.titulo(), data.descricao());
        projeto.id = new ProjetoId();

    var payload = new ProjetoCriadoEvent.Payload(projeto.getTitulo(), projeto.getDescricao());
    projeto.addEvent(new ProjetoCriadoEvent(projeto, payload));

    return projeto;
  }

  public void alterarDadosDoProjeto(AlterarProjeto data) throws DomainException, CommandAlreadyProcessedException {
    if (this.version != data.version()) {
      throw new OptimisticLockingFailureException("Versão do projeto %s inválida.".formatted(this.id));
    }
    if(this.titulo.equals(data.titulo()) && this.descricao.equals(data.descricao())) {
      throw new CommandAlreadyProcessedException("Projeto %s não foi alterado.".formatted(this.id)); 
    }
    this.titulo = data.titulo();
    this.descricao = data.descricao();

    this.version++;
    
    var payload = new ProjetoAlteradoEvent.Payload(this.titulo, this.descricao);
    this.addEvent(new ProjetoAlteradoEvent(this, payload));    
  }
  
  public void excluirProjeto() {
    boolean existeTarefaPendente = tarefas.stream()
        .anyMatch(t -> t.getStatus() == eStatusTarefaVO.PENDENTE);

    if (existeTarefaPendente) {
      throw new DomainException("Não é possível remover o projeto: há pelo menos uma tarefa pendente.");
    }

    Set.copyOf(tarefas).stream()
      .map(t -> {
        t.excluirTarefa(this); // Exclui todas as tarefas antes de excluir o projeto
        this.tarefas.remove(t);
        
        return t.flushEvents();
      })
      .flatMap(Collection::stream)
      .forEach(this::addEvent); // Adiciona os eventos das tarefas ao projeto
    
    this.version++;

    this.addEvent(new ProjetoExcluidoEvent(this, this.id));
  }

  /****** Métodos de negócio de Tarefa ******/

  public void adicionarTarefa(Tarefa novaTarefa) {    
    canAddOneMoreTarefa(); // Verifica se pode adicionar mais uma tarefa

    // if(this.tarefas.contains(novaTarefa)) {
    //   return; // Tarefa ja cadastrada
    // }

    this.tarefas.add(novaTarefa);
    this.version++;

    this.addEvent(new TarefaAdicionadaEvent(this, novaTarefa.getId()));
  }

  public void removerTarefa(TarefaId tarefaId) {
    Tarefa tarefa = getTarefaOrThrow(tarefaId);
    tarefa.excluirTarefa(this);
    
    tarefa.flushEvents().forEach(this::addEvent);

    this.version++;
    this.tarefas.removeIf(t -> t.getId().equals(tarefaId));
  }

  public Tarefa criarNovaTarefaDoProjeto(CriarTarefa data) {
    canAddOneMoreTarefa(); // Verifica se pode adicionar mais uma tarefa

    var tarefa = new Tarefa(
      new TarefaId(),
      data.titulo(),
      data.descricao(),
      data.dataVencimento(),
      data.status(),
      data.prioridade(),
      new HashSet<>(),
      new HashSet<>()
    );

    var payload = new NovaTarefaCriadaEvent.Payload(
        tarefa.getId(), tarefa.getTitulo(), 
        tarefa.getDescricao(), tarefa.getDataVencimento(), 
        tarefa.getStatus(), tarefa.getPrioridade()
      );
      tarefa.addEvent(new NovaTarefaCriadaEvent(this, payload));
      
    // A versão do projeto é incrementada em "adicionarTarefa(tarefa)"
    this.adicionarTarefa(tarefa);

    return tarefa;
  }

  void alterarStatusTarefa(TarefaId tarefaId, eStatusTarefaVO novoStatus, Long projetoVersao, Historico historico) {
    if(this.version != projetoVersao) {
      throw new OptimisticLockingFailureException("Versão do projeto %s inválida.".formatted(this.id));
    }
    getTarefaOrThrow(tarefaId)
        .alterarStatus(this, novoStatus, historico);
    this.version++;
  }

  void alterarDadosTarefa(
    TarefaId tarefaId, 
    String novoTitulo,
    String novaDescricao,
    DataVencimentoVO novaDataVencimento,
    ePrioridadeVO novoPrioridade,
    Long projetoVersao,
    Historico historico
  ) {
    if(this.version != projetoVersao) {
      throw new OptimisticLockingFailureException("Versão do projeto %s inválida.".formatted(this.id));
    }
    getTarefaOrThrow(tarefaId)
        .alterarDados(this, novoTitulo, novaDescricao, novaDataVencimento, novoPrioridade, historico);

    this.version++;
  }

  private Tarefa getTarefaOrThrow(TarefaId tarefaId) {
    return tarefas.stream()
        .filter(t -> t.getId().equals(tarefaId))
        .findFirst()
        .orElseThrow(() -> new DomainException("Tarefa %s não pertence ao projeto %s"
            .formatted(tarefaId.toString(), this.id.toString())));
  }

  private void canAddOneMoreTarefa() {
    if (this.tarefas.size() >= 20) {
      throw new DomainException("Limite de tarefas atingido");
    }
  }

}
