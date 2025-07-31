package com.github.chiarelli.taskmanager.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.chiarelli.taskmanager.domain.dto.CriarTarefa;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.HistoricoAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.StatusTarefaAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAlteradaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.CommandAlreadyProcessedException;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Tarefa extends BaseModel {

  @EqualsAndHashCode.Include
  private TarefaId id;

  @Size(min = 8, max = 100, message = "O titulo deve ter entre 3 e 100 caracteres")
  @NotBlank(message = "O titulo deve ser informado")
  private String titulo;

  @Size(max = 1000, message = "A descricao nao pode ter mais de 1000 caracteres")
  private String descricao;

  @Valid // Valida recursivamente os campos de dataVencimento ao validar Tarefa
  @NotNull
  private DataVencimentoVO dataVencimento;

  @NotNull
  private eStatusTarefaVO status;

  @NotNull
  private ePrioridadeVO prioridade;

  private Set<ComentarioId> comentarios = new HashSet<>();
  private Set<HistoricoId> historicos = new HashSet<>();

  public Tarefa(String titulo, String descricao, DataVencimentoVO dataVencimento,
      eStatusTarefaVO status, ePrioridadeVO prioridade) {
    this.titulo = titulo;
    this.descricao = descricao;
    this.dataVencimento = dataVencimento;
    this.status = status;
    this.prioridade = prioridade;
  }

  // Métodos de negócio
  void alterarStatus(Projeto projeto, eStatusTarefaVO novoStatus, Historico historico) {
    if (this.status == novoStatus) {
      throw new DomainException("Status já se encontra como '" + novoStatus + "'");
    }
    eStatusTarefaVO antigoStatus = this.status;
    this.status = novoStatus;
    adicionarHistorico(projeto, historico);

    var payload = new StatusTarefaAlteradoEvent.Payload(this.id, this.status, antigoStatus);
    this.addEvent(new StatusTarefaAlteradoEvent(projeto, payload));
  }

  void alterarDados(
    Projeto projeto,
    String novoTitulo,
    String novaDescricao,
    DataVencimentoVO novaDataVencimento,
    ePrioridadeVO novoPrioridade,
    Historico historico) throws CommandAlreadyProcessedException {

    if (this.titulo.equals(novoTitulo) && this.descricao.equals(novaDescricao)
        && this.dataVencimento.equals(novaDataVencimento) && this.prioridade.equals(novoPrioridade)) {
      throw new CommandAlreadyProcessedException("Tarefa %s nao foi alterada.".formatted(this.id));
    }
    this.titulo = novoTitulo;
    this.descricao = novaDescricao;
    this.dataVencimento = novaDataVencimento;
    this.prioridade = novoPrioridade;

    adicionarHistorico(projeto, historico);

    var payload = new TarefaAlteradaEvent.Payload(this.getId(), this.titulo, this.descricao, 
        this.dataVencimento, this.status, this.prioridade);

    this.addEvent(new TarefaAlteradaEvent(projeto, payload));
  }

  void adicionarComentario(Projeto projeto, Comentario comentario, Historico historico) {
    this.comentarios.add(comentario.getId());
    adicionarHistorico(projeto,historico);

    var payload = new ComentarioAdicionadoEvent.Payload(this.getId(),comentario.getId(),
        comentario.getDataCriacao(), comentario.getTitulo(), 
        comentario.getDescricao(), comentario.getAutor());

    this.addEvent(new ComentarioAdicionadoEvent(projeto, payload));
  }

  void excluirTarefa(Projeto projeto) {
    if(status == eStatusTarefaVO.PENDENTE) {
      throw new DomainException("Tarefa com status pendente não pode ser excluida.");
    }
    this.addEvent(new TarefaExcluidaEvent(projeto, this.id));
  }

  // Metodos auxiliares
  static Tarefa criarNovaTarefa(CriarTarefa data) {
    var tarefa = new Tarefa();
        tarefa.id = new TarefaId();
        tarefa.titulo = data.titulo();
        tarefa.descricao = data.descricao();
        tarefa.dataVencimento = data.dataVencimento();
        tarefa.status = data.status();
        tarefa.prioridade = data.prioridade();

    return tarefa;
  }

  private void adicionarHistorico(Projeto projeto, Historico historico) {
    this.historicos.add(historico.getId());
    
    var payload = new HistoricoAdicionadoEvent.Payload(this.getId(), historico.getId(),
    historico.getDataOcorrencia(), historico.getTitulo(), 
    historico.getDescricao(), historico.getAutor());
    
    this.addEvent(new HistoricoAdicionadoEvent(projeto, payload));
  }
  
  // Métodos getters
  public Set<ComentarioId> getComentarios() {
    return Collections.unmodifiableSet(comentarios);
  }
  
  public Set<HistoricoId> getHistoricos() {
    return Collections.unmodifiableSet(historicos);
  }

}
