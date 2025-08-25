package com.github.chiarelli.taskmanager.application.dtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.TarefaConcluidaPorVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class TarefaDTO {

  @EqualsAndHashCode.Include
  private TarefaId id;
  
  private String titulo;
  private String descricao;
  private DataVencimentoVO dataVencimento;
  private eStatusTarefaVO status;
  private ePrioridadeVO prioridade;
  private TarefaConcluidaPorVO concluidaPor;
  private Long version = 0L;

  List<ComentarioId> comentarios = new ArrayList<>();
  List<HistoricoId> historicos = new ArrayList<>();

  public List<ComentarioId> getComentarios() {
    return Collections.unmodifiableList(comentarios);
  }

  public List<HistoricoId> getHistoricos() {
    return Collections.unmodifiableList(historicos);
  }

  public Optional<TarefaConcluidaPorVO> getConcluidaPor() {
    return Optional.ofNullable(concluidaPor);
  }

  /**
   * Converts a Tarefa object into a TarefaDTO object, excluding the collections.
   *
   * @param tarefa The Tarefa object to be converted.
   * @return A TarefaDTO object containing the same basic field values as the
   *         Tarefa object,
   *         but without the comentarios and historicos collections.
   */
  public static TarefaDTO fromIgnoringCollections(Tarefa tarefa) {
    var target = new TarefaDTO();
    target.setId(tarefa.getId());
    target.setTitulo(tarefa.getTitulo());
    target.setDescricao(tarefa.getDescricao());
    target.setDataVencimento(tarefa.getDataVencimento());
    target.setStatus(tarefa.getStatus());
    target.setPrioridade(tarefa.getPrioridade());

    tarefa.getConcluidaPor().ifPresent(concluidoPor -> target.setConcluidaPor(concluidoPor));

    return target;
  }

}
