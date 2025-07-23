package com.github.chiarelli.taskmanager.domain.event;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;

public class ComentarioCriadoEvent extends AbstractDomainEvent<ComentarioCriadoEvent.Payload> {

  public ComentarioCriadoEvent(Comentario aggregate, Payload payload) {
    super(aggregate, payload);
  }

  public static record Payload(
    TarefaId tarefaAddedId,
    ComentarioId id,
    Date dataCriacao,
    String titulo,
    String descricao,
    AutorId autor
  ) { }
}
