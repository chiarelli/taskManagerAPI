package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarProjetoPorIdQuery;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuscarProjetoPorIdUseCase implements QueryHandler<BuscarProjetoPorIdQuery, ProjetoDTO> {

  private final iProjetoRepository projetoRepository;

  @Override
  public ProjetoDTO handle(BuscarProjetoPorIdQuery query) {
    query.validate(); // valida os dados da query

    Projeto projeto = projetoRepository.findById(query.projetoId())
        .orElseThrow(() -> new NotFoundException("Projeto %s nao encontrado".formatted(query.projetoId())));
    
    return BuscarProjetoPorIdQuery.toProjetoDTO(projeto);
  }

}
