package com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ComentarioDocument;

@Repository
public interface ComentarioMongoRepository extends MongoRepository<ComentarioDocument, UUID> {

  List<ComentarioDocument> findAllByTarefaId(UUID tarefaId);

  Optional<ComentarioDocument> findByIdAndTarefaId(UUID id, UUID tarefaId);
  
}
