package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.StatusTarefaAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarStatusTarefaCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.exception.OptimisticLockingFailureException;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.model.TarefaService;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class AlterarStatusTarefaUseCaseTest {

  @Mock
  private iTarefasRepository tarefaRepository;

  @Mock
  private iProjetoRepository projetoRepository;

  @Mock
  private Mediator mediator;
  
  @InjectMocks
  @Spy
  private DomainEventsDispatcher dispatcher;
  
  private AlterarStatusTarefaUseCase useCase;
  private TarefaService tarefaService;

  private ProjetoId projetoId;
  private TarefaId tarefaId;
  private Tarefa tarefa;

  @BeforeEach
  void setup() {
    iDomainEventBuffer eventBuffer = new DomainEventBufferImpl();
    tarefaService = new TarefaService(tarefaRepository, projetoRepository, eventBuffer);
    useCase = new AlterarStatusTarefaUseCase(tarefaService, dispatcher);
    
    projetoId = new ProjetoId(UUID.randomUUID());
    tarefaId = new TarefaId(UUID.randomUUID());
    tarefa = new Tarefa(tarefaId, "Tarefa Exemplo", "Descrição", DataVencimentoVO.now(), 
        eStatusTarefaVO.EM_ANDAMENTO, ePrioridadeVO.BAIXA, new HashSet<>(), new HashSet<>());
  }

  @Test
  void deveAlterarStatusDaTarefaComSucesso() {
    // Arrange
    var command = new AlterarStatusTarefaCommand(
        projetoId, tarefaId, eStatusTarefaVO.CONCLUIDA, 1L
    );
    Projeto projeto = new Projeto(projetoId, "Projeto Exemplo", "Descrição", 1L, 
        new HashSet<>(Set.of(tarefa)));

    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    // Act
    TarefaDTO dto = useCase.handle(command);

    // Assert
    assertNotNull(dto);
    assertEquals(tarefaId, dto.getId());
    assertEquals("Tarefa Exemplo", dto.getTitulo());
    assertEquals(eStatusTarefaVO.CONCLUIDA, dto.getStatus());

    // Verifica se os repositorios foram chamados
    verify(projetoRepository, atLeastOnce()).save(projeto);
    verify(tarefaRepository, atLeastOnce()).saveHistorico(eq(tarefa.getId()), any(Historico.class));
    
    // Verifica se os eventos foram coletados no dispatcher
    verify(mediator, atLeastOnce()).emit(captor.capture());
    List<Event> eventosCapturados = captor.getAllValues();
    
    assertNotNull(eventosCapturados);
    assertThat(eventosCapturados).hasSize(2);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof HistoricoAdicionadoEventAdapter);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof StatusTarefaAlteradoEventAdapter);

    verify(dispatcher).emitAll();
  }
  
  @Test
  void deveLancarExcecaoSeProjetoNaoForEncontrado() {
    var command = new AlterarStatusTarefaCommand(
        projetoId, tarefaId, eStatusTarefaVO.CONCLUIDA, 1L
    );

    when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());

    var ex = assertThrows(
        DomainException.class,
        () -> useCase.handle(command)
    );

    assertThat((String) ex.getViolations().get("error")).contains("Projeto %s não existe".formatted(projetoId));
  }

  @Test
  void deveLancarExcecaoSeTarefaNaoEstiverNoProjeto() {
    Projeto projeto = new Projeto(projetoId, "Projeto", "Descrição", 1L, new HashSet<>());
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    var command = new AlterarStatusTarefaCommand(
        projetoId, tarefaId, eStatusTarefaVO.CONCLUIDA, 1L
    );

    var ex = assertThrows(
        DomainException.class,
        () -> useCase.handle(command)
    );

    assertThat((String) ex.getViolations().get("error")).contains("Tarefa %s não pertence ao projeto %s".formatted(tarefaId, projetoId));
  }

  @Test
  void deveLancarExcecaoSeVersaoDoProjetoEstiverIncorreta() {
    Projeto projeto = new Projeto(projetoId, "Projeto", "Descrição", 2L, new HashSet<>(Set.of(tarefa)));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    var command = new AlterarStatusTarefaCommand(
        projetoId, tarefaId, eStatusTarefaVO.CONCLUIDA, 1L // versão desatualizada
    );

    var ex = assertThrows(
        OptimisticLockingFailureException.class,
        () -> useCase.handle(command));

    assertThat(ex.getMessage()).contains("Versão do projeto %s inválida".formatted(projetoId)); // personalize conforme o erro de domínio
  }

  @Test
  void deveValidarCommandAntesDeExecutar() {
    var command = org.mockito.Mockito.spy(new AlterarStatusTarefaCommand(
        projetoId, tarefaId, eStatusTarefaVO.CONCLUIDA, 1L
    ));

    Projeto projeto = new Projeto(projetoId, "Projeto", "Descrição", 1L, new HashSet<>(Set.of(tarefa)));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    useCase.handle(command);

    verify(command).validate();
  }
  
}
