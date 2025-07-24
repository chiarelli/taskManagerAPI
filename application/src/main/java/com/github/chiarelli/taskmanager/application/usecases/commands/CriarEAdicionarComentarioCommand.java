package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.dto.CriarComentario;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarEAdicionarComentarioCommand(
  @NotNull
  ProjetoId projetoId,

  @NotNull
  TarefaId tarefaId,

  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,
  
  @NotBlank(message = "O comentário é obrigatório")
  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,
  
  @NotNull
  AutorId autor

) implements Command<ComentarioDTO> {

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

  public static CriarComentario toCriarComentario(CriarEAdicionarComentarioCommand command) {
    return new CriarComentario(command.tarefaId, command.titulo, command.descricao, command.autor);
  }

}
