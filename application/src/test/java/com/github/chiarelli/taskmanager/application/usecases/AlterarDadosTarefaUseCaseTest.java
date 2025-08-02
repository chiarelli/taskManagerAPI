package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.HistoricoAdicionadoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.TarefaAlteradaEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosTarefaCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.exception.CommandAlreadyProcessedException;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.exception.OptimisticLockingFailureException;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.model.TarefaService;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
class AlterarDadosTarefaUseCaseTest {

  @Mock
  private iTarefasRepository tarefaRepository;

  @Mock
  private iProjetoRepository projetoRepository;

  @Mock
  private Mediator mediator;
  
  @InjectMocks
  @Spy
  private DomainEventsDispatcher dispatcher;

  private TarefaService tarefaService;
  private AlterarDadosTarefaUseCase useCase;

  private Projeto projeto;
  private Tarefa tarefa;
  private AlterarDadosTarefaCommand command;

  @BeforeEach
  void setup() {
    iDomainEventBuffer eventBuffer = new DomainEventBufferImpl();
    
    tarefaService = new TarefaService(tarefaRepository, projetoRepository, eventBuffer);
    useCase = new AlterarDadosTarefaUseCase(tarefaService, projetoRepository, dispatcher);

    var projetoId = new ProjetoId(UUID.randomUUID());
    var tarefaId = new TarefaId(UUID.randomUUID());

    tarefa = new Tarefa(
      tarefaId,
      "Título original",
      "Descrição original",
      DataVencimentoVO.now(),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.MEDIA,
      new HashSet<>(),
      new HashSet<>()
    );

    projeto = new Projeto(projetoId, "Projeto Exemplo", "Desc", 0L, 
        new HashSet<>(Set.of(tarefa)));

    command = new AlterarDadosTarefaCommand(
      projetoId,
      tarefaId,
      "Novo título",
      "Nova descrição",
      DataVencimentoVO.now(),
      ePrioridadeVO.ALTA,
      0L
    );
    
  }

  @Test
  void deveAlterarDadosDaTarefaEEmitirEventos() {
    // Arrange
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));
    when(projetoRepository.findTarefaByProjetoId(projeto.getId(), tarefa.getId())).thenReturn(Optional.of(tarefa));

    // Act
    TarefaDTO dto = useCase.handle(command);
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    // Assert básicos
    assertNotNull(dto);
    assertEquals(command.titulo(), dto.getTitulo());
    assertEquals(command.descricao(), dto.getDescricao());

    // Verifica se os eventos foram coletados no dispatcher
    verify(mediator, atLeastOnce()).emit(captor.capture());
    verify(dispatcher).emitAll();

    List<Event> eventosCapturados = captor.getAllValues();

    assertNotNull(eventosCapturados);
    assertFalse(eventosCapturados.isEmpty());
    assertThat(eventosCapturados).anyMatch(e -> e instanceof HistoricoAdicionadoEventAdapter);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof TarefaAlteradaEventAdapter);
  }

  @Test
  void deveLancarExcecaoQuandoTarefaNaoEncontradaNoProjeto() {
    // Arrange
    var projetoId = new ProjetoId(UUID.randomUUID());

    projeto = new Projeto(projetoId, "Projeto Exemplo", "Desc", 0L, 
        new HashSet<>());
    
    command = new AlterarDadosTarefaCommand(
      projetoId,
      new TarefaId(UUID.randomUUID()),
      "Novo título",
      "Nova descrição",
      DataVencimentoVO.now(),
      ePrioridadeVO.ALTA,
      0L
    );

    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    // Act & Assert
    DomainException ex = assertThrows(DomainException.class, () -> useCase.handle(command));

    assertEquals(1, ex.getViolations().size());
    assertThat(ex.getViolations())
      .containsEntry("error", "Tarefa %s não pertence ao projeto %s".formatted(command.tarefaId(), command.projetoId()));
    
    // Verifica se os eventos foram coletados no dispatcher
    verify(mediator, never()).emit(captor.capture());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveIgnorarEmitirEventosQuandoCommandJaFoiProcessado() {
    // Arrange
    iTarefaService tarefaServiceMock = mock(iTarefaService.class);
    useCase = new AlterarDadosTarefaUseCase(tarefaServiceMock, projetoRepository, dispatcher);

    when(projetoRepository.findTarefaByProjetoId(projeto.getId(), tarefa.getId())).thenReturn(Optional.of(tarefa));

    when(tarefaServiceMock.alterarDadosComHistorico(any(), any()))
        .thenThrow(new CommandAlreadyProcessedException("já processado"));
    
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    // Act
    TarefaDTO dto = useCase.handle(command);

    // Assert
    assertNotNull(dto);
    verify(dispatcher, never()).emitAll(); // eventos não são enviados

    // Verifica se os eventos foram coletados no dispatcher
    verify(mediator, never()).emit(captor.capture());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveLancarExcecaoQuandoCommandEhInvalido() {
    AlterarDadosTarefaCommand commandInvalido = new AlterarDadosTarefaCommand(
      null, // projetoId
      null, // tarefaId
      "",   // título vazio
      "",   // descrição vazia
      null, // data vencimento
      null, // prioridade
      null  // versão
    );

    DomainException ex = assertThrows(DomainException.class, () -> {
      commandInvalido.validate();
    });

    assertEquals(5, ex.getViolations().size());
    assertThat(ex.getViolations().get("projetoId")).isEqualTo("não deve ser nulo");
    assertThat(ex.getViolations().get("tarefaId")).isEqualTo("não deve ser nulo");
    assertThat(ex.getViolations().get("titulo")).isEqualTo("O título deve ter entre 8 e 100 caracteres");
    assertThat(ex.getViolations().get("dataVencimento")).isEqualTo("não deve ser nulo");
    assertThat(ex.getViolations().get("prioridade")).isEqualTo("não deve ser nulo");
  }

  @Test
  void devePersistirHistoricoAoAlterarDadosDaTarefa() {
    // Arrange
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));
    when(projetoRepository.findTarefaByProjetoId(projeto.getId(), tarefa.getId())).thenReturn(Optional.of(tarefa));

    // Act
    useCase.handle(command);

    // Assert
    verify(tarefaRepository).saveHistorico(eq(command.tarefaId()), any(Historico.class));
  }

  @Test
  void deveLancarExcecaoQuandoVersaoEstiverDesatualizada() {
    // Arrange
    command = new AlterarDadosTarefaCommand(
      projeto.getId(),
      tarefa.getId(),
      "Novo título",
      "Nova descrição",
      DataVencimentoVO.now(),
      ePrioridadeVO.ALTA,
      2L
    );

    projeto = new Projeto(projeto.getId(), "Projeto XPTO", "descricao", 0L, new HashSet<>());
    projeto.adicionarTarefa(tarefa); // versão foi para 1L

    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

    // Act & Assert
    OptimisticLockingFailureException ex = assertThrows(OptimisticLockingFailureException.class, () -> useCase.handle(command));

    System.out.println(ex.getMessage());
    assertThat(ex.getMessage()).contains("Versão do projeto %s inválida".formatted(projeto.getId()));

    verify(projetoRepository, never()).save(any(Projeto.class));
    verify(tarefaRepository, never()).saveHistorico(any(TarefaId.class), any(Historico.class));
    verify(dispatcher, never()).collectFrom(any(Projeto.class));
    verify(dispatcher, never()).collectFrom(any(Tarefa.class));
  }
  
}