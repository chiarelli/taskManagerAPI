package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class DescricaoTarefaAlteradaEvent extends AbstractDomainEvent<DescricaoTarefaAlteradaEvent.Payload> {

  public DescricaoTarefaAlteradaEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(TarefaId tarefaId, String novaDescricao) {}

}
