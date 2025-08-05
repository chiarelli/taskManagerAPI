package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.shared.QueryHandler;
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarTarefaPorIdQuery;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuscarTarefaPorIdUseCase implements QueryHandler<BuscarTarefaPorIdQuery, TarefaDTO> {

  private final iProjetoRepository projetoRepository;
  private final ITarefaReaderRepository tarefaReaderRepository;

  @Override
  public TarefaDTO handle(BuscarTarefaPorIdQuery query) {
    query.validate(); // valida os dados da query

    if(!projetoRepository.existsById(query.projetoId())) {
      throw new NotFoundException("Projeto %s nao encontrado".formatted(query.projetoId()));
    }

    Tarefa tarefa = projetoRepository.findTarefaByProjetoId(query.projetoId(), query.tarefaId())
        .orElseThrow(() -> new NotFoundException("Tarefa %s nao encontrada no projeto %s".formatted(query.tarefaId(), query.projetoId())));

    TarefaDTO dto = BuscarTarefaPorIdQuery.toTarefaDTO(tarefa);

    var comentariosIds = tarefaReaderRepository.findAllComentariosIdsByTarefaId(tarefa.getId());
    var historicosIds = tarefaReaderRepository.findAllHistoricosIdsByTarefaId(tarefa.getId());

    dto.setComentarios(comentariosIds);
    dto.setHistoricos(historicosIds);

    return dto;
  }

}
