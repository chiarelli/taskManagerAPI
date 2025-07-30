package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ProjetoAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosProjetoCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class AlterarDadosProjetoUseCaseTest {

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  Mediator mediator;

  @InjectMocks
  DomainEventsDispatcher dispatcher;

  AlterarDadosProjetoUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new AlterarDadosProjetoUseCase(projetoRepository, dispatcher);
  }

  @Test
  void deveAlterarDadosDoProjetoComSucessoQuandoDadosForemValidos() {
    // Arrange
    final String newTitle = "Projeto Atualizado";
    final String newDescription = "Descricao atualizada";
    
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    var projetoId = new ProjetoId(UUID.randomUUID());
    var projetoExistente = new Projeto(projetoId, "Projeto Antigo", "Descricao antiga", 1L, new HashSet<>());
    
    var command = new AlterarDadosProjetoCommand(
        projetoId,
        newTitle,
        newDescription,
        1L
    );

    // simula que o projeto já existe no repositório
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projetoExistente));

    // Act
    var dto = useCase.handle(command);
    
    // verifique se o dispatcher foi chamado
    verify(mediator, atLeastOnce()).emit(captor.capture());

    // Assert
    assertThat(captor.getAllValues())
      .anyMatch(e -> e instanceof ProjetoAlteradoEventAdapter);

    assertNotNull(dto);
    assertEquals(newTitle, dto.getTitulo());
    assertEquals(newDescription, dto.getDescricao());
    assertEquals(2L, dto.getVersion());
    assertEquals(dto.getId().toString(), projetoId.getId().toString());

    // verifique se o projeto foi salvo
    verify(projetoRepository).save(projetoExistente);
  }

  @Test
  void deveLancarNotFoundExceptionQuandoProjetoNaoForEncontrado() {
    // Arrange
    var projetoId = new ProjetoId(UUID.randomUUID());

    var command = new AlterarDadosProjetoCommand(
        projetoId,
        "Qualquer Título",
        "Qualquer Descrição",
        1L);

    when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException ex = assertThrows(
        NotFoundException.class,
        () -> useCase.handle(command));

    assertEquals("Projeto " + projetoId + " nao encontrado", ex.getMessage());

    verify(mediator, never()).emit(any());
    verify(projetoRepository, never()).save(any());
  }


  @Test
  void naoDeveEmitirEventosQuandoDadosNaoForemAlterados() {
    final var projetoId = new ProjetoId(UUID.randomUUID());
    final var titulo = "Mesmo Título";
    final var descricao = "Mesma Descrição";

    var projeto = new Projeto(projetoId, titulo, descricao, 1L, new HashSet<>());

    var command = new AlterarDadosProjetoCommand(projetoId, titulo, descricao, 1L);

    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    var dto = useCase.handle(command);

    // Asset
    assertEquals(projetoId, dto.getId());
    assertEquals(titulo, dto.getTitulo());
    assertEquals(descricao, dto.getDescricao());
    // A versão deve manter 1
    assertEquals(1L, dto.getVersion());

    // Esperado: salvar pode até ocorrer, mas evento não deve ser emitido
    verify(mediator, never()).emit(any());
  }


}
