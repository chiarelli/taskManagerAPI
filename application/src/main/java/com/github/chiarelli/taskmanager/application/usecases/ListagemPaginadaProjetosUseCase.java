package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.repository.IProjectReaderRepository;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemPaginadaProjetosQuery;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListagemPaginadaProjetosUseCase implements QueryHandler<ListagemPaginadaProjetosQuery, Page<ProjetoDTO>> {

  private final IProjectReaderRepository repository;

  @Override
  public Page<ProjetoDTO>  handle(ListagemPaginadaProjetosQuery query) {
    query.validate(); // valida os dados da query

    return repository.findAllPaginated(query.toPageable());
  }

}
