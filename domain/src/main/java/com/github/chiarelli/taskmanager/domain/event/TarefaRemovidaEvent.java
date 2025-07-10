package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class TarefaRemovidaEvent extends AbstractDomainEvent<TarefaRemovidaEvent.Payload> {

  public TarefaRemovidaEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(ProjetoId projetoId, TarefaId tarefaId) {}

}
