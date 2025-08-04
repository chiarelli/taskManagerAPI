package com.github.chiarelli.taskmanager.presentation.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TaskResponse {

  private final UUID id;
  private final String titulo;
  private final String descricao;

  @JsonProperty("data_vencimento")
  private final OffsetDateTime dataVencimento;

  private final eStatusTarefaVO status;
  private final ePrioridadeVO prioridade;

  @JsonProperty("comentarios_qt")
  private final int comentariosQt;

  public static TaskResponse from(TarefaDTO tarefa) {
    return new TaskResponse(
      tarefa.getId().getId(),
      tarefa.getTitulo(), 
      tarefa.getDescricao(),
      DataVencimentoVO.to(tarefa.getDataVencimento()), 
      tarefa.getStatus(), 
      tarefa.getPrioridade(),
      tarefa.getComentarios().size()
    );
  }

}
