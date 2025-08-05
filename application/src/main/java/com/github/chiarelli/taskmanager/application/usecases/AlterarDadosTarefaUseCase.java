package com.github.chiarelli.taskmanager.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosTarefaCommand;
import com.github.chiarelli.taskmanager.domain.dto.AlterarDadosTarefa;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.exception.CommandAlreadyProcessedException;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlterarDadosTarefaUseCase implements CommandHandler<AlterarDadosTarefaCommand, TarefaDTO> {

  private final iTarefaService tarefaService;
  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public TarefaDTO handle(AlterarDadosTarefaCommand command) {
    command.validate(); // valida o command

    var data = new AlterarDadosTarefa(command.projetoId(), command.tarefaId(), 
        command.titulo(), command.descricao(), command.dataVencimento(), 
        command.version());

    AutorId autorId = new AutorId(UUID.randomUUID().toString()); // TODO: pegar o autor logado

    try {
      var result = tarefaService.alterarDadosComHistorico(data, autorId);
  
      dispatcher.collectFrom(result);
      dispatcher.emitAll();
      
    } catch (CommandAlreadyProcessedException e) {
      // Não precisa enviar o evento, pois o command ja foi processado
    }

    Tarefa tarefa = projetoRepository.findTarefaByProjetoId(command.projetoId(), command.tarefaId())
        .orElseThrow(() -> new NotFoundException("Tarefa %s nao encontrada no projeto %s".formatted(command.tarefaId(), command.projetoId())));

    return TarefaDTO.fromIgnoringCollections(tarefa);
  }

}
