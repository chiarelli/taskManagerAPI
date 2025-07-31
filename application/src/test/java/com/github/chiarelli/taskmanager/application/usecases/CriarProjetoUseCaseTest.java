package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarProjetoCommand;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

@ExtendWith(MockitoExtension.class)
public class CriarProjetoUseCaseTest {

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  EventsDispatcher dispatcher;

  @InjectMocks
  CriarProjetoUseCase useCase;

  @Test
  void deveCriarProjetoComDadosValidos() {
    // Arrange
    var command = new CriarProjetoCommand("Projeto Alpha", "Descrição do Projeto Alpha");

    // Act
    ProjetoDTO resultado = useCase.handle(command);

    // Assert
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(0L, resultado.getVersion());
    assertThat(resultado.getTarefas()).isEmpty();
    assertEquals("Projeto Alpha", resultado.getTitulo());
    assertEquals("Descrição do Projeto Alpha", resultado.getDescricao());

    // Verifica se o repositório foi chamado
    verify(projetoRepository, times(1)).save(any(Projeto.class));

    // Verifica se o dispatcher foi chamado corretamente
    verify(dispatcher, times(1)).collectFrom(any(Projeto.class));
    verify(dispatcher, times(1)).emitAll();
  }
  
  @Test
  void deveLancarExcecaoQuandoTituloForVazio() {
    var command = new CriarProjetoCommand("", "Descrição válida");

    var exception = assertThrows(DomainException.class, () -> {
      new GenericValidator<>(command).assertValid();
      useCase.handle(command);
    });

    Map<String, Object> violations = exception.getViolations();

    assertThat(violations).containsKey("titulo");

    verify(projetoRepository, never()).save(any());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveLancarExcecaoQuandoTituloForMuitoCurto() {
    var command = new CriarProjetoCommand("Oi", "Descrição válida");

    var exception = assertThrows(DomainException.class, () -> {
      new GenericValidator<>(command).assertValid();
      useCase.handle(command);
    });

    Map<String, Object> violations = exception.getViolations();
    List<String> mensagens = violations.values().stream()
        .map(String::valueOf)
        .collect(Collectors.toList());

    assertThat(mensagens).contains("O título deve ter entre 3 e 100 caracteres");
    verify(projetoRepository, never()).save(any());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveLancarExcecaoQuandoDescricaoForMuitoLonga() {
    var descricao = "A".repeat(256);
    var command = new CriarProjetoCommand("Título válido", descricao);

    var exception = assertThrows(DomainException.class, () -> {
      new GenericValidator<>(command).assertValid();
      useCase.handle(command);
    });

    Map<String, Object> violations = exception.getViolations();
    List<String> mensagens = violations.values().stream()
        .map(String::valueOf)
        .collect(Collectors.toList());

    assertThat(mensagens).contains("A descrição não pode ter mais de 255 caracteres");
    verify(projetoRepository, never()).save(any());
    verify(dispatcher, never()).emitAll();
  }

}
