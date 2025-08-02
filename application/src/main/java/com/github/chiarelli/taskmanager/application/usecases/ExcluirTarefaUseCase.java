package com.github.chiarelli.taskmanager.application.usecases;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirTarefaCommand;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcluirTarefaUseCase implements CommandHandler<ExcluirTarefaCommand, Void> {

  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public Void handle(ExcluirTarefaCommand command) {
    command.validate(); // valida o command

    Optional<Tarefa> tarefa = projetoRepository.findTarefaByProjetoId(command.projetoId(), command.tarefaId());

    if (tarefa.isEmpty()) {
      return null;
    }

    projetoRepository.findById(command.projetoId())
        .ifPresent(projeto -> {
          projeto.removerTarefa(command.tarefaId());
          projetoRepository.save(projeto);

          dispatcher.collectFrom(projeto);
          dispatcher.emitAll();
        });

    return null;
  }

}
