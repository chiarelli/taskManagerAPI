package com.github.chiarelli.taskmanager.domain.repository;

import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;

public interface iTarefasRepository {

  List<Historico> findAllHistoricosByTarefaId(TarefaId id);

  List<Comentario> findAllComentariosByTarefaId(TarefaId id);

  Optional<Comentario> findComentarioByComentarioIdAndTarefaId(TarefaId id, ComentarioId comentarioId);

  void saveHistorico(TarefaId tarefaId, Historico historico);

  void saveComentario(TarefaId tarefaId, Comentario comentario);
  
}
