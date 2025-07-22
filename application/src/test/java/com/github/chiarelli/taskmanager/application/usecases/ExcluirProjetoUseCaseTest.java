package com.github.chiarelli.taskmanager.application.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirProjetoCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class ExcluirProjetoUseCaseTest {

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  Mediator mediator;

  DomainEventsDispatcher dispatcher;
  
  ExcluirProjetoUseCase useCase;

  @BeforeEach
  void setUp() {
    dispatcher = new DomainEventsDispatcher(mediator);
    useCase = new ExcluirProjetoUseCase(projetoRepository, dispatcher);
  }

  @Test
  void excluirProjetoComSucesso() {
    // Arrange
    var projectId = new ProjetoId();
    var projeto = new Projeto(projectId, "Projeto X", "Descrição", 0L, new HashSet<>());

    when(projetoRepository.findById(projectId)).thenReturn(Optional.of(projeto));
    
    // Act
    var command = new ExcluirProjetoCommand(projectId);
    useCase.handle(command);

    // Assert
    verify(projetoRepository).findById(projectId);
    verify(projetoRepository).delete(projeto);
  }

  @Test
  void excluirProjetoIdNaoEncontrado() {
    // Arrange
    var projectIdNaoEncontrado = new ProjetoId();

    // Simula que o projeto não existe
    when(projetoRepository.findById(projectIdNaoEncontrado)).thenReturn(Optional.empty());

    // Act
    // Usa o mesmo ID no comando
    var command = new ExcluirProjetoCommand(projectIdNaoEncontrado);
    useCase.handle(command);

    // Assert
    // Pode verificar que nenhum erro ocorreu, e nenhuma ação foi feita
    verify(projetoRepository).findById(projectIdNaoEncontrado);
    verify(projetoRepository, never()).delete(any());
  }

}
