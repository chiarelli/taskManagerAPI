package com.github.chiarelli.taskmanager.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.dto.CriarTarefa;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAlteradaEvent;
import com.github.chiarelli.taskmanager.domain.event.NovaTarefaCriadaEvent;
import com.github.chiarelli.taskmanager.domain.event.StatusTarefaAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Tarefa extends AbstractModelEvents implements iDefaultAggregate {

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

  @NotNull
  @Min(0)
  private Long version = 0L;

  private Set<ComentarioId> comentarios = new HashSet<>();
  private Set<HistoricoId> historicos = new HashSet<>();

  public Tarefa(String titulo, String descricao, DataVencimentoVO dataVencimento,
      eStatusTarefaVO status, ePrioridadeVO prioridade, Long version) {
    this.titulo = titulo;
    this.descricao = descricao;
    this.dataVencimento = dataVencimento;
    this.status = status;
    this.prioridade = prioridade;
    this.version = version;
  }

  // Métodos de negócio
  public void criarNovaTarefa(CriarTarefa data) {
    this.id = new TarefaId();
    this.titulo = data.titulo();
    this.descricao = data.descricao();
    this.dataVencimento = data.dataVencimento();
    this.status = data.status();
    this.prioridade = data.prioridade();
    this.version = 0L;

    var payload = new NovaTarefaCriadaEvent.Payload(this.titulo, this.descricao,
        this.dataVencimento, this.status, this.prioridade);

    this.addEvent(new NovaTarefaCriadaEvent(this, payload));
  }

  void alterarStatus(eStatusTarefaVO novoStatus, HistoricoId historicoId) {
    if (this.status == novoStatus) {
      throw new DomainException("Status já se encontra como '" + novoStatus + "'");
    }
    this.status = novoStatus;
    this.historicos.add(historicoId); // Apenas associa o ID
    this.version++;

    var payload = new StatusTarefaAlteradoEvent.Payload(this.id, this.status);
    this.addEvent(new StatusTarefaAlteradoEvent(this, payload));
  }

  void alterarDescricao(String novaDescricao, HistoricoId historicoId) {
    if (Objects.equals(this.descricao, novaDescricao)) {
      return;
    }
    this.descricao = novaDescricao;
    this.historicos.add(historicoId); // Apenas associa o ID
    this.version++;

    var payload = new AlterarTarefa(this.titulo, this.descricao, this.dataVencimento,
        this.status, this.prioridade);

    this.addEvent(new TarefaAlteradaEvent(this, payload));
  }

  void adicionarComentario(ComentarioId comentarioId, HistoricoId historicoId) {
    this.comentarios.add(comentarioId);
    this.historicos.add(historicoId); // Apenas associa o ID
    this.version++;

    this.addEvent(new ComentarioAdicionadoEvent(this, comentarioId));
  }

  void excluirTarefa() {
    if(status != eStatusTarefaVO.PENDENTE) {
      throw new DomainException("Tarefa com status diferente de pendente");
    }
    this.addEvent(new TarefaExcluidaEvent(this, this.id));
  }

  // Métodos getters
  public Set<ComentarioId> getComentarios() {
    return Collections.unmodifiableSet(comentarios);
  }

  public Set<HistoricoId> getHistoricos() {
    return Collections.unmodifiableSet(historicos);
  }

  @Override
  public String getIdAsString() {
    return this.id.toString();
  }

}
