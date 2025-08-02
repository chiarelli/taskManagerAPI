package com.github.chiarelli.taskmanager.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarStatusTarefaCommand;
import com.github.chiarelli.taskmanager.domain.dto.AlterarStatusTarefa;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlterarStatusTarefaUseCase implements CommandHandler<AlterarStatusTarefaCommand, TarefaDTO> {
  
  private final iTarefaService tarefaService;
  private final EventsDispatcher dispatcher;

  @Override
  public TarefaDTO handle(AlterarStatusTarefaCommand command) {
    command.validate(); // valida o command

    var data = new AlterarStatusTarefa(command.projetoId(), command.tarefaId(), 
        command.status(), command.version());

    var autor = new AutorId(UUID.randomUUID().toString()); // TODO: pegar o autor logado
    
    var result = tarefaService.alterarStatusComHistorico(data, autor);

    dispatcher.collectFrom(result);
    dispatcher.emitAll();

    return TarefaDTO.fromIgnoringCollections(result.result());    
  }

}
