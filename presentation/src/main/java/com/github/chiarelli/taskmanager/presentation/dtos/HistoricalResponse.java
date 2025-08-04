package com.github.chiarelli.taskmanager.presentation.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HistoricalResponse {

  private final UUID id;

  @JsonProperty("data_ocorrencia")
  private final LocalDateTime dataOcorrencia;
  
  private final String titulo;
  private final String descricao;  
  private final AutorResponse autor;

  public static HistoricalResponse form(HistoricoDTO historico) {
    return new HistoricalResponse(
      historico.getId().getId(),
      historico.getDataOcorrencia(),
      historico.getTitulo(),
      historico.getDescricao(),
      AutorResponse.from(historico.getAutor())
    );
  }

}
