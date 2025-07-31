package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.constraints.Min;

public record AlterarStatusTarefaCommand(
  eStatusTarefaVO status,

  @Min(value = 0, message = "A versão é obrigatória")
  Long version
  
) implements Command<TarefaDTO> {

}
