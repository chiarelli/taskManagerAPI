package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;

public record AlterarComentario(
  ComentarioId id,
  String titulo,
  String descricao
) {

}
