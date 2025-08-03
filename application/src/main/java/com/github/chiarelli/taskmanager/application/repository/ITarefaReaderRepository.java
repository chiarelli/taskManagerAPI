package com.github.chiarelli.taskmanager.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTOWithAutorId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;

public interface ITarefaReaderRepository {

  Page<HistoricoDTOWithAutorId> findAllHistoricosByTarefaId(TarefaId tarefaId, Pageable pageable);
  
}
