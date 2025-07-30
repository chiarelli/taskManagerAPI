package com.github.chiarelli.taskmanager.presentation.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
  @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,

  @Min(value = 0, message = "A versão é obrigatória")
  Long version
) {

}
