package com.github.chiarelli.taskmanager.application.usecases;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemTarefasDoProjetoQuery;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListagemTarefasDoProjetoUseCase implements QueryHandler<ListagemTarefasDoProjetoQuery, List<TarefaDTO>> {

  private final iProjetoRepository projetoRepository;
  private final ITarefaReaderRepository tarefaReaderRepository;

  @Override
  public List<TarefaDTO> handle(ListagemTarefasDoProjetoQuery query) {
    query.validate(); // valida os dados da query

    if(!projetoRepository.existsById(query.projetoId())) {
      throw new NotFoundException("Projeto %s nao encontrado".formatted(query.projetoId()));
    }

    return projetoRepository.findAllTarefasByProjetoId(query.projetoId())
      .stream()
      .map(ListagemTarefasDoProjetoQuery::toTarefaDTO)
      .peek(dto -> {
        // recupera os ids dos comentarios e historicos
        var comentariosIds = tarefaReaderRepository.findAllComentariosIdsByTarefaId(dto.getId());
        var historicosIds = tarefaReaderRepository.findAllHistoricosIdsByTarefaId(dto.getId());
          dto.setComentarios(comentariosIds);
          dto.setHistoricos(historicosIds);
      })
      .toList();
    
  }

}
