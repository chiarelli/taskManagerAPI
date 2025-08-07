package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.AutorDTO;
import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTO;
import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTOWithAutorId;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemHistoricosDaTarefaQuery;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListagemHistoricosDaTarefaUseCase implements QueryHandler<ListagemHistoricosDaTarefaQuery, Page<HistoricoDTO>> {

  private final ITarefaReaderRepository repository;

  @Override
  public Page<HistoricoDTO> handle(ListagemHistoricosDaTarefaQuery query) {
    query.validate(); // valida os dados da query
    Pageable pageable = query.toPageable();

    Pageable comOrdenacao = PageRequest.of(
      pageable.getPageNumber(),
      pageable.getPageSize(),
      Sort.by(Sort.Direction.DESC, "dataOcorrencia")
    );

    Page<HistoricoDTOWithAutorId> result = repository.findAllHistoricosByTarefaId(query.tarefaId(), comOrdenacao);    
    
    return result.map(h -> { 
      AutorDTO autor = new AutorDTO(h.getAutorId(), "Fake name"); // TODO: pegar o autor logado
      return HistoricoDTOWithAutorId.from(h, autor);
    });
  }

}
