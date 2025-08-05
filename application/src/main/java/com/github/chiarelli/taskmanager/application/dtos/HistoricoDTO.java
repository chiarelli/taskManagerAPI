package com.github.chiarelli.taskmanager.application.dtos;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.model.Historico;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class HistoricoDTO {

  @EqualsAndHashCode.Include
  private final HistoricoId id;
  
  private final LocalDateTime dataOcorrencia;
  private final String titulo;
  private final String descricao;  
  private final AutorDTO autor;

  public static HistoricoDTO from(Historico historico, AutorDTO autor) {
    return new HistoricoDTO(
      historico.getId(), 
      historico.getDataOcorrencia()
          .toInstant()
          .atOffset(ZoneOffset.UTC)
          .toLocalDateTime(), 
      historico.getTitulo(),
      historico.getDescricao(), 
      autor
      );
  }

}
