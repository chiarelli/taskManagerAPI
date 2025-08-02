package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.TarefaExcluidaEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirTarefaCommand;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class ExcluirTarefaUseCaseTest {

  @Mock iProjetoRepository projetoRepository;

  @Mock Mediator mediator;

  @InjectMocks @Spy DomainEventsDispatcher dispatcher;

  ExcluirTarefaUseCase useCase;

  ProjetoId projetoId;
  TarefaId tarefaId;
  Projeto projeto;
  Tarefa tarefa;

  @BeforeEach
  void setUp() {
    useCase = new ExcluirTarefaUseCase(projetoRepository, dispatcher);

    tarefaId = new TarefaId();
    projetoId = new ProjetoId();

    tarefa = new Tarefa(
        tarefaId,
        "Título da Tarefa",
        "Descrição",
        DataVencimentoVO.now(),
        eStatusTarefaVO.CONCLUIDA,
        ePrioridadeVO.ALTA,
        new HashSet<>(),
        new HashSet<>()
    );

    projeto = new Projeto(projetoId, "Projeto X", "Descrição", 0L, new HashSet<>(List.of(tarefa)));
  }

  @Test
  void deveRemoverTarefaComSucessoQuandoExistirNoProjeto() {
    // arrange
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    var command = new ExcluirTarefaCommand(projetoId, tarefaId);

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.of(tarefa));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    // act
    useCase.handle(command);
    
    // assert
    verify(mediator, atLeastOnce()).emit(captor.capture());
    List<Event> eventosCapturados = captor.getAllValues();

    assertThat(projeto.getTarefas()).doesNotContain(tarefa);
    verify(projetoRepository).save(projeto);
    verify(dispatcher, atLeastOnce()).emitAll();

    assertThat(eventosCapturados).isNotEmpty();
    assertThat(eventosCapturados).hasSize(1);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof TarefaExcluidaEventAdapter);
  }

  @Test
  void naoDeveFazerNadaQuandoTarefaNaoExistirNoProjeto() {
    // arrange
    var command = new ExcluirTarefaCommand(projetoId, tarefaId);

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.empty());

    // act
    useCase.handle(command);

    // assert
    verify(projetoRepository, never()).findById(any());
    verify(projetoRepository, never()).save(any());
    verify(mediator, never()).emit(any());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void naoDeveLancarExcecaoQuandoProjetoNaoForEncontrado() {
    // arrange
    var command = new ExcluirTarefaCommand(projetoId, tarefaId);

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.of(tarefa));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());

    // act + assert
    assertThatCode(() -> useCase.handle(command)).doesNotThrowAnyException();

    verify(projetoRepository, never()).save(any());
    verify(mediator, never()).emit(any());
    verify(dispatcher, never()).emitAll();
  }

  @Test
  void deveValidarOComandoAntesDeExecutar() {
    // arrange
    var command = spy(new ExcluirTarefaCommand(projetoId, tarefaId));

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.empty());

    // act
    useCase.handle(command);

    // assert
    verify(command).validate();
  }

  @Test
  void deveLancarDomainExceptionQuandoCommandPossuirCamposNulos() {
    // arrange
    var command = new ExcluirTarefaCommand(null, null);

    // act + assert
    var ex = assertThrows(DomainException.class, () -> useCase.handle(command));

    assertThat(ex.getViolations()).isNotEmpty();
    assertThat((String) ex.getViolations().get("tarefaId")).contains("O id da tarefa é obrigatorio");
    assertThat((String) ex.getViolations().get("projetoId")).contains("O id do projeto é obrigatorio");

    // Garantir que nenhuma interação com o repositório ocorreu
    verify(projetoRepository, never()).save(any());
    verify(mediator, never()).emit(any());
    verify(dispatcher, never()).emitAll();
  }

}
