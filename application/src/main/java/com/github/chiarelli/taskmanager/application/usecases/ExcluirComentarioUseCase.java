package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirComentarioCommand;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcluirComentarioUseCase implements CommandHandler<ExcluirComentarioCommand, Void> {
  
  private final iTarefaService tarefaService;
  private final EventsDispatcher dispatcher;

  @Override
  public Void handle(ExcluirComentarioCommand command) {
    command.validate(); // valida o command

    var result = tarefaService.excluirComentarioComHistorico(command.projetoId(), 
        command.tarefaId(), command.comentarioId());

    dispatcher.collectFrom(result);
    dispatcher.emitAll();

    return null;
  }

}
