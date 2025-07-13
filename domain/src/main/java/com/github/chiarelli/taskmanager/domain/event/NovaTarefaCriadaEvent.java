package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class NovaTarefaCriadaEvent extends AbstractDomainEvent<NovaTarefaCriadaEvent.Payload> {

  public NovaTarefaCriadaEvent(Tarefa tarefa, Payload payload) {
    super(tarefa, payload);
  }

  public static record Payload(
      // TarefaId e version estao na superclasse
      String titulo,
      String descricao,
      DataVencimentoVO dataVencimento,
      eStatusTarefaVO status,
      ePrioridadeVO prioridade
  ){ }

}