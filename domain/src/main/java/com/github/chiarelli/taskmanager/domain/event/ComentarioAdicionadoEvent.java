package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class ComentarioAdicionadoEvent extends AbstractDomainEvent<ComentarioId> {

  public ComentarioAdicionadoEvent(iDefaultAggregate aggregate, ComentarioId comentarioId) {
    super(aggregate, comentarioId);
  }

}
