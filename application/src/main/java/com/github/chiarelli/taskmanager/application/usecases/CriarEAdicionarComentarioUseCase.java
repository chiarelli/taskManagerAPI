package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.AutorDTO;
import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarEAdicionarComentarioCommand;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CriarEAdicionarComentarioUseCase implements CommandHandler<CriarEAdicionarComentarioCommand, ComentarioDTO> {

  private final EventsDispatcher dispatcher;
  private final iTarefaService tarefasService;

  @Override
  public ComentarioDTO handle(CriarEAdicionarComentarioCommand command) {
    command.validate(); // valida o command

    var data = CriarEAdicionarComentarioCommand.toCriarComentario(command);

    var result = tarefasService.adicionarComentarioComHistorico(
      command.projetoId(), command.tarefaId(), data);

    dispatcher.collectFrom(result);
    dispatcher.emitAll();

    var autor = new AutorDTO(command.autor(), "Fake name"); // TODO: pegar o autor logado

    return ComentarioDTO.from(result.result(), autor);    
  }

}
