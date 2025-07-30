package com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.TarefaDocument;

public class TarefaMapper {

  public static TarefaDocument toDocument(Tarefa tarefa) {
    TarefaDocument doc = new TarefaDocument();
    doc.setId(tarefa.getId().getId());
    doc.setTitulo(tarefa.getTitulo());
    doc.setDescricao(tarefa.getDescricao());
    doc.setStatus(tarefa.getStatus());
    doc.setPrioridade(tarefa.getPrioridade());
    doc.setDataVencimento(tarefa.getDataVencimento().getDataVencimento());
    return doc;
  }

  public static Tarefa toDomain(TarefaDocument doc) {
    Set<ComentarioId> comentarioIds = doc.getComentarioIds().stream()
        .map(ComentarioId::new)
        .collect(Collectors.toSet());

    Set<HistoricoId> historicoIds = doc.getHistoricoIds().stream()
        .map(HistoricoId::new)
        .collect(Collectors.toSet());

    return new Tarefa(
        new TarefaId(doc.getId()),
        doc.getTitulo(),
        doc.getDescricao(),
        new DataVencimentoVO(doc.getDataVencimento()),
        doc.getStatus(),
        doc.getPrioridade(),
        comentarioIds,
        historicoIds);
  }

}
