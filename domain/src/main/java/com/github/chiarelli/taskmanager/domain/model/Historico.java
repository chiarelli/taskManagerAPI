package com.github.chiarelli.taskmanager.domain.model;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Historico {

  @EqualsAndHashCode.Include
  private final HistoricoId id;
  
  private final Date dataOcorrencia;
  private final String titulo;
  private final String descricao;
  
  private final AutorId autor;
  
}
