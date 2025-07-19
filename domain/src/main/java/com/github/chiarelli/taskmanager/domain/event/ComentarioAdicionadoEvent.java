package com.github.chiarelli.taskmanager.domain.event;

import java.time.LocalDateTime;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class ComentarioAdicionadoEvent extends AbstractDomainEvent<ComentarioAdicionadoEvent.Payload> {

  public ComentarioAdicionadoEvent(iDefaultAggregate aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(
    ComentarioId id,
    LocalDateTime dataCriacao,
    String titulo,
    String descricao,
    AutorId autor
  ) { }

}
