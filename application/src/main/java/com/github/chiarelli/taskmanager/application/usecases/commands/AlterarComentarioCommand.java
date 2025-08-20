package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlterarComentarioCommand(

  @NotNull(message = "O id do projeto é obrigatorio")
  ProjetoId projetoId,

  @NotNull(message = "O id da tarefa é obrigatorio")
  TarefaId tarefaId,

  @NotNull(message = "O id do comentario é obrigatorio")
  ComentarioId comentarioId,

  @NotBlank(message = "O título é obrigatório")
  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,
  
  @NotBlank(message = "O comentário é obrigatório")
  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,
  
  @NotNull(message = "O id do autor é obrigatorio")
  AutorId autorId

) implements Command<ComentarioDTO> {

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
