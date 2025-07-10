package com.github.chiarelli.taskmanager.domain.repository;

import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public interface iProjetoRepository {

  Optional<Projeto> findById(ProjetoId id);

  void save(Projeto projeto);
  
  void softDelete(Projeto projeto);

  List<Projeto> findAllByUserId(String userId);

  List<Tarefa> findAllTarefasByProjetoId(ProjetoId idProjeto);
  
}
