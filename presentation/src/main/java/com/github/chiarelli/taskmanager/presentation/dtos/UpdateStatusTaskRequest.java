package com.github.chiarelli.taskmanager.presentation.dtos;

import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusTaskRequest(
  @NotNull(message = "O status da tarefa deve ser informado")
  eStatusTarefaVO status,

  @Min(value = 0, message = "A versão é obrigatória")
  Long version

) { }
