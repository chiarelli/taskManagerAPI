package com.github.chiarelli.taskmanager.domain.model;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;

public class ProjetoCriadoEvent extends AbstractDomainEvent<ProjetoCriadoEvent.Payload> {

  public ProjetoCriadoEvent(Projeto projeto, Payload payload) {
    super(projeto, payload);
  }

  public record Payload(
    String titulo,
    String descricao
  ) {}

}
