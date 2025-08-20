package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.AutorDTO;
import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarComentarioCommand;
import com.github.chiarelli.taskmanager.domain.dto.AlterarComentario;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlterarComentarioUseCase implements CommandHandler<AlterarComentarioCommand, ComentarioDTO> {

  private final iTarefaService tarefaService;
  private final EventsDispatcher dispatcher;

  @Override
  public ComentarioDTO handle(AlterarComentarioCommand command) {
    command.validate(); // valida o command

    var data = new AlterarComentario(command.comentarioId(), 
        command.titulo(), command.descricao());

    ServiceResult<Comentario> result = tarefaService.alterarComentarioComHistorico(
        command.projetoId(), command.tarefaId(), data);
    
    dispatcher.collectFrom(result);
    dispatcher.emitAll();

    Comentario comentario = result.result();

    AutorDTO autor = new AutorDTO(comentario.getAutor(), "Fake name"); // TODO: pegar o autor logado

    return ComentarioDTO.from(comentario, autor);
  }

}
