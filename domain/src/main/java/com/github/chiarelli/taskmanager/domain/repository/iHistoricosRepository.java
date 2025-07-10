package com.github.chiarelli.taskmanager.domain.repository;

import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.model.Historico;

public interface iHistoricosRepository {

  Optional<Historico> findById(HistoricoId id);

  void save(Historico historico);
  
}
