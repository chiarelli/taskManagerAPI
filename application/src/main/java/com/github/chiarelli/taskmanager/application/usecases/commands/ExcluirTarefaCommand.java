package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotNull;

public record ExcluirTarefaCommand(
  @NotNull(message = "O id do projeto é obrigatorio")
  ProjetoId projetoId,

  @NotNull(message = "O id da tarefa é obrigatorio")
  TarefaId tarefaId
) implements Command<Void> {

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }
  
}
