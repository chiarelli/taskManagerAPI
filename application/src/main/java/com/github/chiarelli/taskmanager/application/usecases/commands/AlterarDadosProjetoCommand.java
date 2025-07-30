package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.dto.AlterarProjeto;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlterarDadosProjetoCommand(

  @NotNull(message = "O id do projeto é obrigatório")
  ProjetoId projetoId,
  
  @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
  @NotBlank(message = "O título é obrigatório")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,

  @Min(value = 0, message = "A versão é obrigatória")
  Long version

) implements Command<ProjetoDTO> { 

  public AlterarProjeto toAlterarProjeto() {
    return new AlterarProjeto(projetoId, titulo, descricao, version);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}