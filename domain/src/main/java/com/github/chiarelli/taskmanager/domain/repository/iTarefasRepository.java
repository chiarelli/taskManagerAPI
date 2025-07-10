package com.github.chiarelli.taskmanager.domain.repository;

import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public interface iTarefasRepository {

  Optional<Tarefa> findById(TarefaId id);

  void save(Tarefa tarefa);

  void delete(Tarefa tarefa);

  List<Historico> findAllHistoricoByTarefaId(TarefaId id);

  List<Comentario> findAllComentariosByTarefaId(TarefaId id);
  
}
