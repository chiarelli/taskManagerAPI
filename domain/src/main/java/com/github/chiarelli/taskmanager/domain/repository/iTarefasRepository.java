package com.github.chiarelli.taskmanager.domain.repository;

import java.util.List;

import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;

public interface iTarefasRepository {

  List<Historico> findAllHistoricosByTarefaId(TarefaId id);

  List<Comentario> findAllComentariosByTarefaId(TarefaId id);

  void saveHistorico(TarefaId tarefaId, Historico historico);

  void saveComentario(TarefaId tarefaId, Comentario comentario);
  
}
