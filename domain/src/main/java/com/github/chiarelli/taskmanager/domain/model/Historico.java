package com.github.chiarelli.taskmanager.domain.model;

import java.time.LocalDateTime;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Historico {

  private final HistoricoId id;
  private final LocalDateTime dataOcorrencia;
  private final String titulo;
  private final String descricao;
  
  private final AutorId autor;
  
}
