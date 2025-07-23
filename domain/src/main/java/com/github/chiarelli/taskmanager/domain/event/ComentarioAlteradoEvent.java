package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.Comentario;

public class ComentarioAlteradoEvent extends AbstractDomainEvent<ComentarioAlteradoEvent.Payload> {
  
  public ComentarioAlteradoEvent(Comentario comentario, Payload payload) {
    super(comentario, payload);
  }

  public static record Payload(String titulo, String descricao) {}

}
