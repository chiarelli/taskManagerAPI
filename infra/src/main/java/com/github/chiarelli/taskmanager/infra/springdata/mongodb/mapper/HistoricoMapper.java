package com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;

@Component
public class HistoricoMapper {

  public static HistoricoDocument toDocument(Historico historico) {
    var autorId = UUID.fromString(historico.getAutor().getId());

    var target = new HistoricoDocument();
      target.setId(historico.getId().getId());
      target.setDataOcorrencia(historico.getDataOcorrencia());
      target.setTitulo(historico.getTitulo());
      target.setDescricao(historico.getDescricao());
      target.setAutorId(autorId);
      
    return target;
  }

  public static Historico toDomain(HistoricoDocument doc) {
    return new Historico(
      new HistoricoId(doc.getId()),
      doc.getDataOcorrencia(),
      doc.getTitulo(),
      doc.getDescricao(),
      new AutorId(doc.getAutorId().toString())
    );
  }

}
