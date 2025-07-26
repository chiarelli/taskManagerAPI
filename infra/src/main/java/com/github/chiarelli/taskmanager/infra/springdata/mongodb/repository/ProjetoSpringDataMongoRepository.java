package com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ProjetoDocument;

@Repository
public interface ProjetoSpringDataMongoRepository extends MongoRepository<ProjetoDocument, UUID> {

  // List<ProjetoDocument> findAllByUserId(String userId);

  // @Query(value = "{ '_id': ?0 }", update = "{ '$pull': { 'tarefas': { 'id': ?1 } } }")
  // @Modifying
  // void removeTarefaById(String projetoId, String tarefaId);

}
