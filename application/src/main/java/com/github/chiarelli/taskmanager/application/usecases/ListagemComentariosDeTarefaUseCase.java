package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.AutorDTO;
import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTOWithAuthorId;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemComentariosDeTarefaQuery;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListagemComentariosDeTarefaUseCase implements QueryHandler<ListagemComentariosDeTarefaQuery, Page<ComentarioDTO>> {
  
  private final ITarefaReaderRepository repository;
  private final iProjetoRepository projetoRepository;
  
  @Override
  public Page<ComentarioDTO> handle(ListagemComentariosDeTarefaQuery query) {
    query.validate(); // valida os dados da query

    Pageable pageable = query.toPageable();

    Pageable comOrdenacao = PageRequest.of(
      pageable.getPageNumber(),
      pageable.getPageSize(),
      Sort.by(Sort.Direction.DESC, "dataOcorrencia")
    );

    projetoRepository.findTarefaByProjetoId(query.projetoId(), query.tarefaId())
      .orElseThrow(() -> new NotFoundException("Tarefa %s nao encontrada no projeto %s".formatted(query.tarefaId(), query.projetoId())));
    
    Page<ComentarioDTOWithAuthorId> result = repository.findAllComentariosByTarefaId(query.tarefaId(), comOrdenacao);

    return result.map(c -> {      
      AutorDTO autor = new AutorDTO(c.getAutorId(), "Fake name"); // TODO: pegar o autor logado
      return ComentarioDTOWithAuthorId.from(c, autor);
    });
  }

}
