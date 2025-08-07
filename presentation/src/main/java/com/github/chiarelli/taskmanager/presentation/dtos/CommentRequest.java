package com.github.chiarelli.taskmanager.presentation.dtos;

import jakarta.validation.constraints.Size;

public record CommentRequest(
  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao
) {

}
