package com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "historicos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HistoricoDocument {

  @Id
  @EqualsAndHashCode.Include
  private UUID id;

  private Date dataOcorrencia;
  private String titulo;
  private String descricao;

  private UUID tarefaId;
  private UUID autorId;
  
}
