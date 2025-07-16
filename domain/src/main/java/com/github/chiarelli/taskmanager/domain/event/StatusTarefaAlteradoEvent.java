package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class StatusTarefaAlteradoEvent extends AbstractDomainEvent<StatusTarefaAlteradoEvent.Payload> {

  public StatusTarefaAlteradoEvent(Projeto projeto, Payload payload) {
    super(projeto, payload);
  }

  public static record Payload(TarefaId tarefaId, eStatusTarefaVO novoStatus, eStatusTarefaVO antigoStatus){}
  
}
