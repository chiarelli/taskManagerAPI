package com.github.chiarelli.taskmanager.presentation.dtos;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,

  @JsonProperty("data_vencimento")
  @NotNull(message = "A data de vencimento deve ser informada")
  OffsetDateTime dataVencimento,
  
  @NotNull(message = "O status da tarefa deve ser informado")
  eStatusTarefaVO status,

  @NotNull(message = "A prioridade da tarefa deve ser informada")
  ePrioridadeVO prioridade
) { }
