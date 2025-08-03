package com.github.chiarelli.taskmanager.application.dtos;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.model.Historico;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HistoricoDTOWithAutorId {

  private final HistoricoId id;
  private final LocalDateTime dataOcorrencia;
  private final String titulo;
  private final String descricao;  
  private final AutorId autorId;

  public static HistoricoDTO from(HistoricoDTOWithAutorId historico, AutorDTO autor) {
    return new HistoricoDTO(
      historico.getId(), 
      historico.getDataOcorrencia(), 
      historico.getTitulo(),
      historico.getDescricao(), 
      autor
      );
  }

  public static HistoricoDTOWithAutorId of(Historico historico) {
    return new HistoricoDTOWithAutorId(
      historico.getId(), 
      historico.getDataOcorrencia().toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime(), 
      historico.getTitulo(),
      historico.getDescricao(), 
      historico.getAutor()
      );
  }

}
