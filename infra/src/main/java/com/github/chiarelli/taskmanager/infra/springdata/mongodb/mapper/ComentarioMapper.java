package com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTOWithAuthorId;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ComentarioDocument;

@Component
public class ComentarioMapper {

  public static Comentario toDomain(ComentarioDocument doc) {
    return new Comentario(
      new ComentarioId(doc.getId()),
      doc.getDataOcorrencia(),
      doc.getTitulo(),
      doc.getDescricao(),
      new AutorId(doc.getAutorId().toString()),
      new TarefaId(doc.getTarefaId()),
      doc.getVersion()
    );
  }

  public static ComentarioDocument toDocument(Comentario comentario) {
    var autorId = UUID.fromString(comentario.getAutor().getId());
    var version = comentario.getVersion() -1;

    ComentarioDocument doc = new ComentarioDocument();
      doc.setId(comentario.getId().getId());
      doc.setTitulo(comentario.getTitulo());
      doc.setDescricao(comentario.getDescricao());
      doc.setVersion(comentario.getVersion() == 0L ? null : version);
      doc.setDataOcorrencia(comentario.getDataCriacao());
      doc.setTarefaId(comentario.getTarefaId().getId());
      doc.setAutorId(autorId);
      
    return doc;
  }

  public static ComentarioDTOWithAuthorId toDTOWithAuthorId(ComentarioDocument doc) {
    return new ComentarioDTOWithAuthorId(new ComentarioId(doc.getId()), doc.getDataOcorrencia(), 
        doc.getTitulo(), doc.getDescricao(), new AutorId(doc.getAutorId().toString()));
  }
}
