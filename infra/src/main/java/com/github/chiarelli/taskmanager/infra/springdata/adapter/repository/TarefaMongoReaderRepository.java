package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTOWithAutorId;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.HistoricoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.HistoricoMongoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TarefaMongoReaderRepository implements ITarefaReaderRepository {

  private final HistoricoMongoRepository mongoRepository;

  @Override
  public Page<HistoricoDTOWithAutorId> findAllHistoricosByTarefaId(TarefaId tarefaId, Pageable pageable) {
    Page<HistoricoDocument> result = mongoRepository.findAllByTarefaId(tarefaId.getId(), pageable);

    return result.map(HistoricoMapper::toDTOWithAutorId);
  }

}
