package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public class NovaTarefaCriadaEvent extends AbstractDomainEvent<TarefaId> {

  public NovaTarefaCriadaEvent(Tarefa tarefa, TarefaId id) {
    super(tarefa, id);
  }

}
