package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.Projeto;

public class ProjetoAlteradoEvent extends AbstractDomainEvent<ProjetoAlteradoEvent.Payload> {
  
  public ProjetoAlteradoEvent(Projeto projeto, Payload payload) {
    super(projeto, payload);
  }

  public static record Payload(
    String titulo,
    String descricao
  ) {}
}
