package com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "projetos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjetoDocument {

  @Id
  @EqualsAndHashCode.Include
  private UUID id;

  private String titulo;
  private String descricao;

  @Version
  private Long version;

  private Set<TarefaDocument> tarefas = new HashSet<>();

  @Override
  public String toString() {
    return "ProjetoDocument{" +
        "id=" + id +
        ", titulo='" + titulo + '\'' +
        ", descricao='" + descricao + '\'' +
        ", version=" + version +
        '}';
  }

}
