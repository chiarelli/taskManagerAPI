package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;

public class TarefaExcluidaEvent extends AbstractDomainEvent<TarefaId> {

  public TarefaExcluidaEvent(Projeto projeto, TarefaId tarefaId) {
    super(projeto, tarefaId);
  }

}
