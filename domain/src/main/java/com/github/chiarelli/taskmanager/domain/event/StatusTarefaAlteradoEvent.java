package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class StatusTarefaAlteradoEvent extends AbstractDomainEvent<StatusTarefaAlteradoEvent.Payload> {

  public StatusTarefaAlteradoEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(TarefaId tarefaId, eStatusTarefaVO novoStatus){}
  
}
