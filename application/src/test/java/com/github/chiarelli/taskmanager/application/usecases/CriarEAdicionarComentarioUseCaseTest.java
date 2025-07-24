package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarEAdicionarComentarioCommand;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.ComentarioCriadoEvent;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.event.HistoricoAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.model.TarefaService;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iEventFlusher;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class CriarEAdicionarComentarioUseCaseTest {

  @Mock
  EventsDispatcher dispatcher;

  @Mock
  iProjetoRepository projetoRepository;

  @Mock
  iTarefasRepository tarefaRepository;

  @InjectMocks
  DomainEventBufferImpl eventBuffer;
  
  @InjectMocks
  CriarEAdicionarComentarioUseCase useCase;
  
  TarefaService tarefasService;

  Projeto projeto;
  Tarefa tarefa;

  @BeforeEach
  void setUp() {
    tarefasService = new TarefaService(tarefaRepository, projetoRepository, eventBuffer);
    useCase = new CriarEAdicionarComentarioUseCase(dispatcher, tarefasService);

    tarefa = new Tarefa(new TarefaId(), "Title", "Description", DataVencimentoVO.of(OffsetDateTime.now().plusDays(1)), eStatusTarefaVO.PENDENTE, ePrioridadeVO.BAIXA, new HashSet<>(), new HashSet<>());
    
    var tarefas = new HashSet<Tarefa>();
        tarefas.add(tarefa);

    projeto = new Projeto(new ProjetoId(), "TitledBorder", "Description", 0L, tarefas);
  }

  @Test
  void deveAdicionarComentarioComDadosValidos() {
    // Arrange
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));
    
    var autorId = new AutorId(UUID.randomUUID().toString());

    var command = new CriarEAdicionarComentarioCommand(
      projeto.getId(),
      tarefa.getId(),
      "Título do comentário",
      "Comentário válido e bem formado",
      autorId
    );

    // Act
    ComentarioDTO dto = useCase.handle(command);

    ArgumentCaptor<iEventFlusher> captor = ArgumentCaptor.forClass(iEventFlusher.class);
    verify(dispatcher, times(1)).collectFrom(captor.capture());
    
    // Extrai os eventos de todos os agregados
    List<iEventFlusher> flushed = captor.getAllValues();

    List<AbstractDomainEvent<?>> todosEventos = flushed.stream()
      .flatMap(agg -> agg.flushEvents().stream())
      .collect(Collectors.toList());

    ComentarioId commentIdAdded = tarefa.getComentarios().stream().findFirst()
      .orElseThrow(() -> new RuntimeException("Nenhum comentário adicionado"));

    // Assert
    assertNotNull(dto);
    assertEquals(commentIdAdded, dto.getId());
    assertEquals("Título do comentário", dto.getTitulo());
    assertEquals("Comentário válido e bem formado", dto.getDescricao());
    assertEquals(autorId, dto.getAutor().getId());
    assertEquals("Fake name", dto.getAutor().getNome());

    // Verifica se os eventos foram emitidos
    assertThat(todosEventos)
      .anyMatch(e -> e instanceof ComentarioCriadoEvent);
    assertThat(todosEventos)
      .anyMatch(e -> e instanceof ComentarioAdicionadoEvent);
    assertThat(todosEventos)
      .anyMatch(e -> e instanceof HistoricoAdicionadoEvent);

    // Verifica que os métodos de dispatcher foram chamados
    verify(dispatcher).collectFrom(flushed.get(0));
    verify(dispatcher).emitAll();
  }

  @Test
  void deveLancarExcecaoSeProjetoNaoForEncontrado() {
    // Arrange
    var projetoIdInexistente = new ProjetoId();
    var tarefaId = new TarefaId();
    var autorId = new AutorId(UUID.randomUUID().toString());

    var command = new CriarEAdicionarComentarioCommand(
        projetoIdInexistente,
        tarefaId,
        "Qualquer título",
        "Qualquer descrição",
        autorId);

    when(projetoRepository.findById(projetoIdInexistente)).thenReturn(Optional.empty());

    // Act & Assert
    var ex = assertThrows(
        DomainException.class,
        () -> useCase.handle(command));

    assertTrue(ex.getViolations().containsKey("error"));
    assertEquals("Projeto %s não existe".formatted(projetoIdInexistente), ex.getViolations().get("error"));
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveLancarExcecaoSeTarefaNaoForEncontrada() {
    // Arrange
    var tarefaIdInexistente = new TarefaId();
    var autorId = new AutorId(UUID.randomUUID().toString());

    var projetoComTarefasVazias = new Projeto(projeto.getId(), "Nome", "Desc", 0L, new HashSet<>());

    var command = new CriarEAdicionarComentarioCommand(
        projeto.getId(),
        tarefaIdInexistente,
        "Qualquer título",
        "Descrição",
        autorId);

    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projetoComTarefasVazias));

    // Act & Assert
    var ex = assertThrows(
        DomainException.class,
        () -> useCase.handle(command));

    assertTrue(ex.getViolations().containsKey("error"));
    assertEquals("Tarefa %s não pertence ao projeto %s".formatted(tarefaIdInexistente, projeto.getId()),
        ex.getViolations().get("error"));
    verify(dispatcher, never()).emitAll();
  }

}
