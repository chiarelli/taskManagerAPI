package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTOWithAuthorId;
import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTOWithAutorId;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ComentarioDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.ComentarioMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.HistoricoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.IdProjection;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ComentarioMongoRepository;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.HistoricoMongoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TarefaMongoReaderRepository implements ITarefaReaderRepository {

  private final HistoricoMongoRepository historicoMongoRepository;
  private final ComentarioMongoRepository comentarioMongoRepository;

  @Override
  public Page<HistoricoDTOWithAutorId> findAllHistoricosByTarefaId(TarefaId tarefaId, Pageable pageable) {
    Page<HistoricoDocument> result = historicoMongoRepository.findAllByTarefaId(tarefaId.getId(), pageable);

    return result.map(HistoricoMapper::toDTOWithAutorId);
  }

  @Override
  public List<HistoricoId> findAllHistoricosIdsByTarefaId(TarefaId tarefaId) {
    return historicoMongoRepository.findOnlyIdsByTarefaId(tarefaId.getId())
        .stream()
        .map(IdProjection::getId)
        .map(HistoricoId::new)
        .toList();
  }

  @Override
  public List<ComentarioId> findAllComentariosIdsByTarefaId(TarefaId tarefaId) {
    return comentarioMongoRepository.findOnlyIdsByTarefaId(tarefaId.getId())
        .stream()
        .map(IdProjection::getId)
        .map(ComentarioId::new)
        .toList();
  }

  @Override
  public Page<ComentarioDTOWithAuthorId> findAllComentariosByTarefaId(TarefaId tarefaId, Pageable pageable) {
    Page<ComentarioDocument> result = comentarioMongoRepository.findAllByTarefaId(tarefaId.getId(), pageable);

    return result.map(ComentarioMapper::toDTOWithAuthorId);
  }

}
