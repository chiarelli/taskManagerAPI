package com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ProjetoDocument;

public class ProjetoMapper {

  public static ProjetoDocument toDocument(Projeto projeto) {
    ProjetoDocument doc = new ProjetoDocument();
    doc.setId(projeto.getId().getId());
    doc.setTitulo(projeto.getTitulo());
    doc.setDescricao(projeto.getDescricao());

    Long mongoVersion = projeto.getVersion() == 0L ? null : projeto.getVersion() - 1;
    doc.setVersion(mongoVersion);

    if (projeto.getTarefas() != null) {
      doc.setTarefas(projeto.getTarefas().stream()
          .map(TarefaMapper::toDocument)
          .collect(Collectors.toSet()));
    }

    return doc;
  }

  public static Projeto toDomain(ProjetoDocument doc) {
    Set<Tarefa> tarefas = (doc.getTarefas() != null)
        ? doc.getTarefas().stream()
            .map(TarefaMapper::toDomain)
            .collect(Collectors.toSet())
        : new HashSet<>();

    return new Projeto(
        new ProjetoId(doc.getId()),
        doc.getTitulo(),
        doc.getDescricao(),
        doc.getVersion(),
        tarefas);
  }

  public static ProjetoDTO toDTO(ProjetoDocument doc) {
    return ProjetoDTO.from(toDomain(doc));
  }

}
