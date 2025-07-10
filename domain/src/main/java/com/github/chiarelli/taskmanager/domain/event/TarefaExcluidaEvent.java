package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class TarefaExcluidaEvent extends AbstractDomainEvent<TarefaId> {

  public TarefaExcluidaEvent(iDefaultAggregate aggregate, TarefaId tarefaId) {
    super(aggregate, tarefaId);
  }

}
