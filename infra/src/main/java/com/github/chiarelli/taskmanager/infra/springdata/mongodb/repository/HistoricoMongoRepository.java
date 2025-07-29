package com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;

@Repository
public interface HistoricoMongoRepository extends MongoRepository<HistoricoDocument, UUID> {

  List<HistoricoDocument> findAllByTarefaId(UUID tarefaId);
  
}
