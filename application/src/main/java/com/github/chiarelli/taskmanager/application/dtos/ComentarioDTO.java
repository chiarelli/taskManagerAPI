package com.github.chiarelli.taskmanager.application.dtos;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ComentarioDTO {

  private final ComentarioId id;
  private final Date dataCriacao;
  private final String titulo;
  private final String descricao;
  private final AutorDTO autor;

  public static ComentarioDTO from(Comentario comentario, AutorDTO autor) {
    return new ComentarioDTO(comentario.getId(), comentario.getDataCriacao(), 
        comentario.getTitulo(), comentario.getDescricao(), autor);
  }

}
