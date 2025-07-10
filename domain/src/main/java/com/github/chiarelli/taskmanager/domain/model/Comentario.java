package com.github.chiarelli.taskmanager.domain.model;

import java.time.LocalDateTime;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Comentario {

  private final ComentarioId id;
  private final LocalDateTime dataCriacao;
  private final String titulo;
  private final String descricao;

  private final AutorId autor;
}
