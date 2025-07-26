package com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TarefaDocument {

  @EqualsAndHashCode.Include
  private UUID id;

  private String titulo;
  private String descricao;
  private eStatusTarefaVO status;     // "PENDENTE", etc.
  private ePrioridadeVO prioridade; // "ALTA", etc.
  private Date dataVencimento;

  private Set<UUID> comentarioIds = new HashSet<>();
  private Set<UUID> historicoIds = new HashSet<>();

  @Override
  public String toString() {
    return "TarefaDocument{" +
        "id=" + id +
        ", titulo='" + titulo + '\'' +
        ", descricao='" + descricao + '\'' +
        ", status=" + status +
        ", prioridade=" + prioridade +
        ", dataVencimento=" + dataVencimento +
        '}';
  }

}
