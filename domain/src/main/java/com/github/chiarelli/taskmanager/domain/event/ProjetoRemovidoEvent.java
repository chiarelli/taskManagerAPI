package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class ProjetoRemovidoEvent extends AbstractDomainEvent<ProjetoId> {

  public ProjetoRemovidoEvent(iDefaultAggregate aggregate, ProjetoId projetoId) {
    super(aggregate, projetoId);
  }

}
