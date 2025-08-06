package com.github.chiarelli.taskmanager.application.dtos;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class ComentarioDTOWithAuthorId {

  @EqualsAndHashCode.Include
  private final ComentarioId id;
  
  private final Date dataCriacao;
  private final String titulo;
  private final String descricao;
  private final AutorId autorId;

  public static ComentarioDTO from(ComentarioDTOWithAuthorId c, AutorDTO autor) {
    return new ComentarioDTO(c.getId(), c.getDataCriacao(), c.getTitulo(), c.getDescricao(), autor);
  }

}
