package com.github.chiarelli.taskmanager.domain.event;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class HistoricoAdicionadoEvent extends AbstractDomainEvent<HistoricoAdicionadoEvent.Payload> {

  public HistoricoAdicionadoEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(
    HistoricoId id,
    Date dataOcorrencia,
    String titulo,
    String descricao,
    AutorId autor
  ) {};

}
