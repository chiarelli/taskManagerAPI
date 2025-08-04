package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusTarefaCommand(
  @NotNull
  ProjetoId projetoId,

  @NotNull
  TarefaId tarefaId,
  
  @NotNull
  eStatusTarefaVO status,

  @NotNull
  @Min(value = 0, message = "A versão não pode ser negativa")
  Long version
  
) implements Command<TarefaDTO> {

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }
  
}
