package com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.IdProjection;

@Repository
public interface HistoricoMongoRepository extends MongoRepository<HistoricoDocument, UUID> {

  List<HistoricoDocument> findAllByTarefaId(UUID tarefaId);

  Page<HistoricoDocument> findAllByTarefaId(UUID tarefaId, Pageable pageable);

  @Query(value = "{ 'tarefaId': ?0 }", fields = "{ '_id' : 1 }")
  List<IdProjection> findOnlyIdsByTarefaId(UUID tarefaId);
  
}
