package com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "comentarios")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ComentarioDocument {

  @Id
  @EqualsAndHashCode.Include
  private UUID id;

  private String titulo;
  private String descricao;
  
  @Version
  private Long version;
  private Date dataOcorrencia;
  
  private UUID tarefaId;
  private UUID autorId;
}
