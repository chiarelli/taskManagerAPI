package com.github.chiarelli.taskmanager.domain.repository;

import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public interface iProjetoRepository {

  Optional<Projeto> findById(ProjetoId id);

  Optional<Tarefa> findTarefaByProjetoId(ProjetoId projetoId, TarefaId tarefaId);

  void save(Projeto projeto);
  
  void delete(Projeto projeto);

  void deleteTarefa(ProjetoId projetoId, TarefaId tarefaId);

  List<Projeto> findAllByUserId(String userId);

  List<Tarefa> findAllTarefasByProjetoId(ProjetoId idProjeto);
  
}
