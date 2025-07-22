package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotNull;

public record ExcluirProjetoCommand(

  @NotNull(message = "O id do projeto nao pode ser nulo")
  ProjetoId projetoId
  
) implements Command<Void> { 

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
