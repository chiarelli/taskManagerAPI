package com.github.chiarelli.taskmanager.domain.model;

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
import com.github.chiarelli.taskmanager.domain.event.ProjetoExcluidoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Projeto extends BaseModel implements iDefaultAggregate {

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

    var payload = new ProjetoCriadoEvent.Payload(projeto.getTitulo(), projeto.getDescricao());
    projeto.addEvent(new ProjetoCriadoEvent(projeto, payload));

    return projeto;
  }

  public void alterarDadosDoProjeto(AlterarProjeto data) {
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
      throw new DomainException("Não é possível remover o projeto: há tarefas pendentes.");
    }

    Set.copyOf(tarefas).forEach(t -> this.removerTarefa(t.getId())); // Exclui todas as tarefas antes de excluir o projeto

    this.addEvent(new ProjetoExcluidoEvent(this, this.id));
  }

  /****** Métodos de negócio de Tarefa ******/

  void adicionarTarefa(Tarefa novaTarefa) {    
    canAddOneMoreTarefa(); // Verifica se pode adicionar mais uma tarefa

    this.tarefas.add(novaTarefa);

    this.addEvent(new TarefaAdicionadaEvent(this, novaTarefa.getId()));
  }

  public void removerTarefa(TarefaId tarefaId) {
    getTarefaOrThrow(tarefaId)    
        .excluirTarefa(this);

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
      0L,
      new HashSet<>(),
      new HashSet<>()
    );

    var payload = new NovaTarefaCriadaEvent.Payload(
        tarefa.getId(), tarefa.getTitulo(), 
        tarefa.getDescricao(), tarefa.getDataVencimento(), 
        tarefa.getStatus(), tarefa.getPrioridade()
      );

    tarefa.addEvent(new NovaTarefaCriadaEvent(this, payload));

    this.adicionarTarefa(tarefa);

    return tarefa;
  }

  void alterarStatusTarefa(TarefaId tarefaId, eStatusTarefaVO novoStatus, Historico historico) {
    getTarefaOrThrow(tarefaId)
        .alterarStatus(this, novoStatus, historico);
  }

  void alterarDescricaoTarefa(TarefaId tarefaId, String novaDescricao, Historico historico) {
    getTarefaOrThrow(tarefaId)
        .alterarDescricao(this, novaDescricao, historico);
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
