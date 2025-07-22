package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarProjetoPorIdQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class BuscarProjetoPorIdUseCaseTest {

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  Mediator mediator;

  @InjectMocks
  DomainEventsDispatcher dispatcher;
  
  @InjectMocks
  BuscarProjetoPorIdUseCase useCase;

  @Test
  void buscarProjetoComSucesso() {
    // Arrange
    ProjetoId projetoId = new ProjetoId();
    Projeto projeto = new Projeto(projetoId, "Projeto X", "Descrição", 0L, new HashSet<>());

    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));
    
    // Act
    var query = new BuscarProjetoPorIdQuery(projetoId);
    ProjetoDTO result = useCase.handle(query);

    // Assert
    assertEquals(projetoId, result.getId());
    assertEquals("Projeto X", result.getTitulo());
    assertEquals("Descrição", result.getDescricao());
    assertEquals(0L, result.getVersion());
  }

  @Test
  void buscarProjetoComIdInvalido() {
    // Arrange
    ProjetoId projetoId = new ProjetoId();
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());
    
    // Act & Assert
    NotFoundException ex = assertThrows(NotFoundException.class, () -> {
      var query = new BuscarProjetoPorIdQuery(projetoId);
      useCase.handle(query);
    });
    assertEquals("Projeto " + projetoId + " nao encontrado", ex.getMessage());
  }

  @Test
  void buscarProjetoComIdNulo() {
    // Act & Assert
    DomainException ex = assertThrows(DomainException.class, () -> {
      var query = new BuscarProjetoPorIdQuery(null);
      useCase.handle(query);
    });
    System.out.println(ex.getViolations());
    assertEquals(1, ex.getViolations().size());

    assertThat(ex.getViolations())
      .containsEntry("projetoId", "O id do projeto nao pode ser nulo");
  }

}
