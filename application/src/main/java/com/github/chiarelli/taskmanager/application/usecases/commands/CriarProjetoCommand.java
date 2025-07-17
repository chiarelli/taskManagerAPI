package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.dto.CriarProjeto;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarProjetoCommand(
  
  @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
  @NotBlank(message = "O título é obrigatório")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao

) implements Command<ProjetoDTO> { 

  public CriarProjeto toCriarProjeto() {
    return new CriarProjeto(titulo, descricao);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
