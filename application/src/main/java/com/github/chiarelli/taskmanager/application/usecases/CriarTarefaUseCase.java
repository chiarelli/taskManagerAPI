package com.github.chiarelli.taskmanager.application.usecases;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.CommandHandler;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarTarefaCommand;
import com.github.chiarelli.taskmanager.domain.dto.CriarTarefa;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CriarTarefaUseCase implements CommandHandler<CriarTarefaCommand, TarefaDTO> {

  private final iProjetoRepository projetoRepository;
  private final EventsDispatcher dispatcher;

  @Override
  public TarefaDTO handle(CriarTarefaCommand command) {
    command.validate(); // valida o command

    Projeto projeto = projetoRepository.findById(command.projetoId())
      .orElseThrow(() -> new NotFoundException("Projeto %s nao encontrado".formatted(command.projetoId())));

    var data = new CriarTarefa(
      command.projetoId(),
      command.titulo(),
      command.descricao(),
      command.vencimento(),
      command.status(),
      command.prioridade()
    );

    Tarefa tarefa = projeto.criarNovaTarefaDoProjeto(data); // Tarefa é criada e adicionada ao projeto

    new GenericValidator<>(tarefa).assertValid(); // valida a tarefa e o projeto,
    new GenericValidator<>(projeto).assertValid(); // em caso de erro, lança uma DomainException

    projetoRepository.save(projeto);

    dispatcher.collectFrom(tarefa);
    dispatcher.collectFrom(projeto);
    dispatcher.emitAll();

    return TarefaDTO.fromIgnoringCollections(tarefa);
  }

}
