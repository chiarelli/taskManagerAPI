package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosProjetoCommand;
import com.github.chiarelli.taskmanager.domain.exception.CommandAlreadyProcessedException;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlterarDadosProjetoUseCase implements CommandHandler<AlterarDadosProjetoCommand, ProjetoDTO> {

  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public ProjetoDTO handle(AlterarDadosProjetoCommand command) throws DomainException, NotFoundException {
    command.validate(); // valida o payload do command
    
    Projeto projeto = projetoRepository.findById(command.projetoId())
      .orElseThrow(() -> new NotFoundException("Projeto %s nao encontrado".formatted(command.projetoId())));
    
    var data = command.toAlterarProjeto();

    try {
      projeto.alterarDadosDoProjeto(data);

      var validator = new GenericValidator<>(projeto);
      validator.assertValid(); // valida o domínio, em caso de erro, lanca uma DomainException
      
      projetoRepository.save(projeto);
      
      dispatcher.collectFrom(projeto);
      dispatcher.emitAll();

    } catch (CommandAlreadyProcessedException e) {
      // Não precisa enviar o evento, pois o command ja foi processado
    }

    return ProjetoDTO.fromWithTarefas(projeto);
  }

}
