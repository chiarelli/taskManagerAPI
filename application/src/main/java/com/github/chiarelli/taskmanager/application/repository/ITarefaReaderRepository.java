package com.github.chiarelli.taskmanager.application.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTOWithAuthorId;
import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTOWithAutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;

public interface ITarefaReaderRepository {

  Page<HistoricoDTOWithAutorId> findAllHistoricosByTarefaId(TarefaId tarefaId, Pageable pageable);

  Page<ComentarioDTOWithAuthorId> findAllComentariosByTarefaId(TarefaId tarefaId, Pageable pageable);

  List<HistoricoId> findAllHistoricosIdsByTarefaId(TarefaId tarefaId);

  List<ComentarioId> findAllComentariosIdsByTarefaId(TarefaId tarefaId);
  
}
