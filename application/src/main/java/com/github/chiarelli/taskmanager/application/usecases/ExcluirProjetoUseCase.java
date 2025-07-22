package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirProjetoCommand;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcluirProjetoUseCase implements CommandHandler<ExcluirProjetoCommand, Void> {

  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public Void handle(ExcluirProjetoCommand command) {
    command.validate(); // valida o command

    projetoRepository
      .findById(command.projetoId())
      .ifPresentOrElse(projeto -> {
        projeto.excluirProjeto();
        new GenericValidator<>(projeto).assertValid();
    
        projetoRepository.delete(projeto);
    
        dispatcher.collectFrom(projeto);
        dispatcher.emitAll();
      }, () -> {});

    return null;
  }

}
