package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class NovaTarefaCriadaEvent extends AbstractDomainEvent<NovaTarefaCriadaEvent.Payload> {

  public NovaTarefaCriadaEvent(Projeto projeto, Payload payload) {
    super(projeto, payload);
  }

  public static record Payload(
     TarefaId tarefaId,
      String titulo,
      String descricao,
      DataVencimentoVO dataVencimento,
      eStatusTarefaVO status,
      ePrioridadeVO prioridade
  ){ }

}