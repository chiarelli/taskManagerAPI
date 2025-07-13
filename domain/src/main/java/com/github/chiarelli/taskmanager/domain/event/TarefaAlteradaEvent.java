package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class TarefaAlteradaEvent extends AbstractDomainEvent<AlterarTarefa> {

  public TarefaAlteradaEvent(iDefaultAggregate aggregate, AlterarTarefa payload) {
    super(aggregate, payload);
  }

}
