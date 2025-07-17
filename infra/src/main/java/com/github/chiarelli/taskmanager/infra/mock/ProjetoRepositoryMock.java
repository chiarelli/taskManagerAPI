package com.github.chiarelli.taskmanager.infra.mock;

import java.util.List;
import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

public class ProjetoRepositoryMock implements iProjetoRepository {

  @Override
  public Optional<Projeto> findById(ProjetoId id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Optional<Tarefa> findTarefaByProjetoId(ProjetoId projetoId, TarefaId tarefaId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findTarefaByProjetoId'");
  }

  @Override
  public void save(Projeto projeto) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

  @Override
  public void delete(Projeto projeto) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  @Override
  public void deleteTarefa(ProjetoId projetoId, TarefaId tarefaId) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'deleteTarefa'");
  }

  @Override
  public List<Projeto> findAllByUserId(String userId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAllByUserId'");
  }

  @Override
  public List<Tarefa> findAllTarefasByProjetoId(ProjetoId idProjeto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAllTarefasByProjetoId'");
  }

}
