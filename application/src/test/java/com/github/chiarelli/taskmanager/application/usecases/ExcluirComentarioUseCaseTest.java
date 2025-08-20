package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioExcluidoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.HistoricoAdicionadoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirComentarioCommand;
import com.github.chiarelli.taskmanager.domain.dto.CriarProjeto;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.model.TarefaService;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import io.github.jkratz55.mediator.core.Mediator;

@ExtendWith(MockitoExtension.class)
public class ExcluirComentarioUseCaseTest {

  @Mock
  private iTarefasRepository tarefaRepository;

  @Mock
  private iProjetoRepository projetoRepository;

  @Mock
  private Mediator mediator;

  @InjectMocks
  @Spy
  private DomainEventsDispatcher dispatcher;

  private iTarefaService tarefaService;
  private ExcluirComentarioUseCase useCase;

  private ProjetoId projetoId;
  private TarefaId tarefaId;
  private ComentarioId comentarioId;

  @BeforeEach
  void setup() {
    tarefaService = new TarefaService(tarefaRepository, projetoRepository, new DomainEventBufferImpl());
    useCase = new ExcluirComentarioUseCase(tarefaService, dispatcher);

    projetoId = new ProjetoId();
    tarefaId = new TarefaId();
    comentarioId = new ComentarioId();
  }

  @Test
  @DisplayName("Deve excluir comentário com sucesso, emitir eventos e retornar void")
  void deveExcluirComentarioComSucesso() {
    // arrange
    Projeto projeto = Projeto.criarNovoProjeto(new CriarProjeto("Projeto Teste", "Descrição Projeto"));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    Tarefa tarefa = new Tarefa(
        tarefaId,
        "Título Tarefa",
        "Descrição Tarefa",
        DataVencimentoVO.now(),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.MEDIA,
        new HashSet<>(),
        new HashSet<>()
    );
    projeto.adicionarTarefa(tarefa);

    AutorId autorId = new AutorId(UUID.randomUUID().toString());
    Comentario comentario = new Comentario(
        comentarioId,
        new Date(),
        "Título Comentário",
        "Descrição Comentário",
        autorId,
        tarefaId
    );

    projeto.flushEvents(); // Limpa eventos externos ao teste
    tarefa.flushEvents();  // Limpa eventos externos ao teste

    when(tarefaRepository.findComentarioByComentarioIdAndTarefaId(tarefaId, comentarioId))
        .thenReturn(Optional.of(comentario));

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    // act
    Void result = useCase.handle(new ExcluirComentarioCommand(projetoId, tarefaId, comentarioId));

    verify(mediator, atLeastOnce()).emit(captor.capture());
    List<Event> eventosCapturados = captor.getAllValues();

    // assert
    assertThat(result).isNull();

    verify(tarefaRepository).deleteComentario(tarefa.getId(), comentarioId);
    verify(tarefaRepository).saveHistorico(eq(tarefaId), any(Historico.class));
    verify(dispatcher).emitAll();

    assertThat(eventosCapturados).hasSize(2);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof ComentarioExcluidoEventAdapter);
    assertThat(eventosCapturados).anyMatch(e -> e instanceof HistoricoAdicionadoEventAdapter);
  }

  @Test
  @DisplayName("Deve lancar excecao quando comentario nao existir")
  void deveLancarExcecaoQuandoComentarioNaoExistir() {
    // arrange
    var comentarioIdNaoExiste = new ComentarioId();

    Projeto projeto = Projeto.criarNovoProjeto(new CriarProjeto("Projeto Teste", "Descrição Projeto"));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    Tarefa tarefa = new Tarefa(
        tarefaId,
        "Título Tarefa",
        "Descrição Tarefa",
        DataVencimentoVO.now(),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.MEDIA,
        new HashSet<>(),
        new HashSet<>()
    );
    projeto.adicionarTarefa(tarefa);

    // act
    Map<String, Object> violations = assertThrows(DomainException.class, () -> useCase.handle(new ExcluirComentarioCommand(projetoId, tarefaId, comentarioIdNaoExiste)))
      .getViolations();

    assertThat(violations).isNotEmpty();
    assertThat(violations).containsEntry("error", "Comentario %s nao pertence à tarefa %s".formatted(comentarioIdNaoExiste, tarefaId));

    verify(dispatcher, never()).emitAll();
    verify(tarefaRepository, never()).saveHistorico(eq(tarefaId), any(Historico.class));
    verify(tarefaRepository, never()).deleteComentario(tarefa.getId(), comentarioId);
  }

  @Test
  @DisplayName("Deve lançar exceção quando projetoId for nulo")
  void deveLancarExcecaoQuandoProjetoIdForNulo() {
    ExcluirComentarioCommand command = new ExcluirComentarioCommand(null, tarefaId, comentarioId);

    DomainException ex = assertThrows(DomainException.class, () -> useCase.handle(command));

    assertThat(ex.getViolations()).containsEntry("projetoId", "O id do projeto é obrigatorio");

    verify(dispatcher, never()).emitAll();
    verify(tarefaRepository, never()).saveHistorico(eq(tarefaId), any(Historico.class));
    verify(tarefaRepository, never()).deleteComentario(tarefaId, comentarioId);
  }

  @Test
  @DisplayName("Deve lançar exceção quando tarefaId for nulo")
  void deveLancarExcecaoQuandoTarefaIdForNulo() {
    ExcluirComentarioCommand command = new ExcluirComentarioCommand(projetoId, null, comentarioId);

    DomainException ex = assertThrows(DomainException.class, () -> useCase.handle(command));

    assertThat(ex.getViolations()).containsEntry("tarefaId", "O id da tarefa é obrigatorio");

    verify(dispatcher, never()).emitAll();
    verify(tarefaRepository, never()).saveHistorico(any(), any(Historico.class));
    verify(tarefaRepository, never()).deleteComentario(any(), eq(comentarioId));
  }

  @Test
  @DisplayName("Deve lançar exceção quando comentarioId for nulo")
  void deveLancarExcecaoQuandoComentarioIdForNulo() {
    ExcluirComentarioCommand command = new ExcluirComentarioCommand(projetoId, tarefaId, null);

    DomainException ex = assertThrows(DomainException.class, () -> useCase.handle(command));

    assertThat(ex.getViolations()).containsEntry("comentarioId", "O id do comentario é obrigatorio");

    verify(dispatcher, never()).emitAll();
    verify(tarefaRepository, never()).saveHistorico(any(), any(Historico.class));
    verify(tarefaRepository, never()).deleteComentario(any(), any(ComentarioId.class));
  }

}
