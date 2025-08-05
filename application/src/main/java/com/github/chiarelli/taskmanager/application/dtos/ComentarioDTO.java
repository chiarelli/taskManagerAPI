package com.github.chiarelli.taskmanager.application.dtos;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class ComentarioDTO {

  @EqualsAndHashCode.Include
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
