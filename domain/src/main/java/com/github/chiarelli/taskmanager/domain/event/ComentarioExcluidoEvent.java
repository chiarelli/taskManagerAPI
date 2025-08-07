package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;

public class ComentarioExcluidoEvent extends AbstractDomainEvent<ComentarioId> {

  public ComentarioExcluidoEvent(Projeto projeto, ComentarioId comentarioId) {
    super(projeto, comentarioId);
  }

}
