package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarTarefaCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.event.NovaTarefaCriadaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.shared.iEventFlusher;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class CriarTarefaUseCaseTest {

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  EventsDispatcher dispatcher;

  @InjectMocks
  CriarTarefaUseCase useCase;

  Projeto projeto;

  @BeforeEach
  void setUp() {
    projeto = new Projeto(new ProjetoId(), "Título do Projeto de teste", 
    "Descrição do Projeto de teste", 0L, new HashSet<>());
  }

  @Test
  void criarTarefaEAdicionarNoProjetoEVerificarSeORepositorioFoiChamado() {
    // Arrange
    var command = new CriarTarefaCommand(
      projeto.getId(),
      "Título da Tarefa",
      "Descrição da Tarefa",
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    when(projetoRepository.findById(projeto.getId()))
      .thenReturn(Optional.of(projeto));

    // Act
    var dto = useCase.handle(command);
      
    ArgumentCaptor<iEventFlusher> captor = ArgumentCaptor.forClass(iEventFlusher.class);
      verify(dispatcher, times(2)).collectFrom(captor.capture());
    
    // Extrai os eventos de todos os agregados
    List<iEventFlusher> flushed = captor.getAllValues();

    List<AbstractDomainEvent<?>> todosEventos = flushed.stream()
      .flatMap(agg -> agg.flushEvents().stream())
      .collect(Collectors.toList());

    // Tenta extrair a tarefa criada do projeto
    var optional = projeto.getTarefas().stream()
      .filter(t -> t.getId().equals(dto.getId()))
      .findFirst();

    // Assert
    verify(projetoRepository).save(projeto);
    verify(dispatcher).collectFrom(projeto);
    verify(dispatcher).collectFrom(any(Tarefa.class));
    verify(dispatcher).emitAll();

    assertThat(todosEventos).size().isEqualTo(2);
    assertThat(todosEventos).anyMatch(e -> e instanceof NovaTarefaCriadaEvent);
    assertThat(todosEventos).anyMatch(e -> e instanceof TarefaAdicionadaEvent);

    assertNotNull(dto);
    assertTrue(optional.isPresent(), "Tarefa nao encontrada no projeto");
    assertEquals(command.titulo(), dto.getTitulo());
    assertEquals(command.descricao(), dto.getDescricao());
    assertEquals(command.status(), dto.getStatus());
    assertEquals(command.prioridade(), dto.getPrioridade());
    assertEquals(command.vencimento(), dto.getDataVencimento());
  }

  @Test
  void naoDeveCriarTarefaSeProjetoNaoForEncontrado() {
    // Arrange
    var idProjeto = new ProjetoId();
    var command = new CriarTarefaCommand(
      idProjeto,
      "Título da Tarefa",
      "Descrição da Tarefa",
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    when(projetoRepository.findById(idProjeto)).thenReturn(Optional.empty());

    // Act & Assert
    var ex = assertThrows(NotFoundException.class,
      () -> useCase.handle(command)
    );

    assertEquals("Projeto %s nao encontrado".formatted(idProjeto), ex.getMessage());

    verify(projetoRepository).findById(idProjeto);
    verifyNoMoreInteractions(projetoRepository, dispatcher);
  }

  @Test
  void devePropagarErroQuandoSalvarProjetoFalhar() {
    when(projetoRepository.findById(projeto.getId()))
      .thenReturn(Optional.of(projeto));

    doThrow(new RuntimeException("Erro ao salvar"))
      .when(projetoRepository).save(any());

    var command = new CriarTarefaCommand(
      projeto.getId(),
      "Título válido",
      "Descrição válida",
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    var ex = assertThrows(RuntimeException.class, () -> useCase.handle(command));
    assertEquals("Erro ao salvar", ex.getMessage());

    verify(projetoRepository).findById(projeto.getId());
    verify(projetoRepository).save(projeto);
  }

}
