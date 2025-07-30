package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.ProjetoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ProjetoSpringDataMongoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjetoMongoRepository implements iProjetoRepository {

  private final ProjetoSpringDataMongoRepository mongoRepository;

  @Override
  public Optional<Projeto> findById(ProjetoId id) {
    return mongoRepository.findById(id.getId())
        .map(ProjetoMapper::toDomain);
  }

  @Override
  public void save(Projeto projeto) {
    mongoRepository.save(ProjetoMapper.toDocument(projeto));
  }

  @Override
  public void delete(Projeto projeto) {
    mongoRepository.deleteById(projeto.getId().getId());
  }

  /* 
  @Override
  public List<Projeto> findAllByUserId(String userId) {
    return mongoRepository.findAllByUserId(userId)
        .stream()
        .map(this::toDomain)
        .toList();
  }
  */

  @Override
  public Optional<Tarefa> findTarefaByProjetoId(ProjetoId projetoId, TarefaId tarefaId) {
    return findById(projetoId)
        .flatMap(p -> p.getTarefas().stream()
          .filter(t -> t.getId().equals(tarefaId))
          .findFirst()
        );
  }

  @Override
  public void deleteTarefa(ProjetoId projetoId, TarefaId tarefaId) {
    findById(projetoId).ifPresent(projeto -> {
      projeto.removerTarefa(tarefaId);
      save(projeto); // Regrava o projeto atualizado
    });
  }

  @Override
  public List<Tarefa> findAllTarefasByProjetoId(ProjetoId idProjeto) {
    Optional<Projeto> optional = findById(idProjeto);

    if (optional.isEmpty()) {
      return List.of();
    }

    return optional.get().getTarefas().stream().toList();
  }

  @Override
  public boolean existsById(ProjetoId id) {
    return mongoRepository.existsById(id.getId());
  }

}
