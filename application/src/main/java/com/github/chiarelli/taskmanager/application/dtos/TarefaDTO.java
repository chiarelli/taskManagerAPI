package com.github.chiarelli.taskmanager.application.dtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TarefaDTO {

  private TarefaId id;
  private String titulo;
  private String descricao;
  private DataVencimentoVO dataVencimento;
  private eStatusTarefaVO status;
  private ePrioridadeVO prioridade;
  private Long version = 0L;

  List<ComentarioDTO> comentarios = new ArrayList<>();
  List<HistoricoDTO> historicos = new ArrayList<>();

  public List<ComentarioDTO> getComentarios() {
    return Collections.unmodifiableList(comentarios);
  }

  public List<HistoricoDTO> getHistoricos() {
    return Collections.unmodifiableList(historicos);
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
    return target;
  }

}
