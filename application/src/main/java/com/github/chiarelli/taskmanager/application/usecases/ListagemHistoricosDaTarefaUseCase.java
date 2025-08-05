package com.github.chiarelli.taskmanager.application.usecases;

import java.util.UUID;

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
import com.github.chiarelli.taskmanager.domain.entity.AutorId;

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
    AutorDTO autor = new AutorDTO(new AutorId(UUID.randomUUID().toString()), "Fake name"); // TODO: pegar o autor logado
    
    return result.map(h -> HistoricoDTOWithAutorId.from(h, autor));
  }

}
