package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarProjetoCommand;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CriarProjetoUseCase implements CommandHandler<CriarProjetoCommand, ProjetoDTO> {

  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public ProjetoDTO handle(CriarProjetoCommand command) {
    command.validate(); // valida o command

    var projeto = Projeto.criarNovoProjeto(command.toCriarProjeto());

    var validator = new GenericValidator<>(projeto);
    validator.assertValid(); // valida o domínio, em caso de erro, lanca uma DomainException

    projetoRepository.save(projeto);

    dispatcher.collectFrom(projeto);
    dispatcher.emitAll();

    return ProjetoDTO.fromIgnoringCollections(projeto);
  }

}
