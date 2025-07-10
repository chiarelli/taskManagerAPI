package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class TarefaAdicionadaEvent extends AbstractDomainEvent<TarefaAdicionadaEvent.Payload> {

  public TarefaAdicionadaEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record  Payload(
      ProjetoId projetoId,
      TarefaId tarefaId
  ) {};

}
