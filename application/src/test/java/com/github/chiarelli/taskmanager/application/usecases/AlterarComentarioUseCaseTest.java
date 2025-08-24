package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
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

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.HistoricoAdicionadoEventAdapter;
import com.github.chiarelli.taskmanager.application.events.DomainEventsDispatcher;
import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarComentarioCommand;
import com.github.chiarelli.taskmanager.domain.dto.CriarProjeto;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
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
public class AlterarComentarioUseCaseTest {

  @Mock
  private Mediator mediator;

  @Mock
  private iTarefasRepository tarefaRepository;

  @Mock
  private iProjetoRepository projetoRepository;

  @InjectMocks
  @Spy
  private DomainEventsDispatcher dispatcher;

  private iTarefaService tarefaService;
  private AlterarComentarioUseCase useCase;

  private ProjetoId projetoId;
  private TarefaId tarefaId;
  private ComentarioId comentarioId;
  private AutorId autorId;

  @BeforeEach
  void setup() {
    tarefaService = new TarefaService(tarefaRepository, projetoRepository, new DomainEventBufferImpl());
    useCase = new AlterarComentarioUseCase(tarefaService, dispatcher);

    projetoId = new ProjetoId();
    tarefaId = new TarefaId();
    comentarioId = new ComentarioId();
    autorId = new AutorId(UUID.randomUUID().toString());
  }

  @Test
  @DisplayName("Deve alterar comentário com sucesso, emitir eventos e retornar DTO")
  void deveAlterarComentarioComSucesso_emitirEventosERetornarDTO() {
    // arrange
    Projeto projeto = Projeto.criarNovoProjeto(new CriarProjeto("Projeto Titulo", "Descrição Projeto"));
    when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

    Tarefa tarefa = new Tarefa(tarefaId, "Titulo antigo", "Descrição antiga", DataVencimentoVO.now(), eStatusTarefaVO.PENDENTE, ePrioridadeVO.BAIXA);

    projeto.adicionarTarefa(tarefa);

    Comentario comentario = new Comentario(comentarioId, new Date(), "Título antigo",
        "Descrição antiga", autorId, tarefaId);

    when(tarefaRepository.findComentarioByComentarioIdAndTarefaId(tarefaId, comentarioId)).thenReturn(Optional.of(comentario));

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

    AlterarComentarioCommand command = new AlterarComentarioCommand(projetoId, tarefaId, comentarioId,
        "Novo título", "Nova descrição", autorId);

    // act
    ComentarioDTO dto = useCase.handle(command);
    
    // assert
    assertThat(dto).isNotNull();
    assertThat(dto.getTitulo()).isEqualTo("Novo título");
    assertThat(dto.getDescricao()).isEqualTo("Nova descrição");
    
    verify(mediator, atLeastOnce()).emit(captor.capture());
    verify(dispatcher).emitAll();

    List<Event> events = captor.getAllValues();

    assertThat(events).hasSize(2);
    assertThat(events).anyMatch(e -> e instanceof ComentarioAlteradoEventAdapter);
    assertThat(events).anyMatch(e -> e instanceof HistoricoAdicionadoEventAdapter);
  }

  @Test
  @DisplayName("Deve lançar exceção quando título for nulo")
  void deveLancarExcecao_quandoTituloForNulo() {
    AlterarComentarioCommand command = new AlterarComentarioCommand(
        projetoId, tarefaId, comentarioId,
        null, "Descrição válida", autorId);

    // act + assert
    Map<String, Object> violations = assertThrows(DomainException.class, 
        () -> useCase.handle(command)).getViolations();

    assertThat(violations).isNotEmpty();
    assertThat(violations).containsEntry("titulo", "O título é obrigatório");
  }

  @Test
  @DisplayName("Deve lançar exceção quando descricao for vazia")
  void deveLancarExcecao_quandoDescricaoForVazia() {
    AlterarComentarioCommand command = new AlterarComentarioCommand(
        projetoId, tarefaId, comentarioId,
        "Título válido", "", autorId);

    // act + assert
    Map<String, Object> violations = assertThrows(DomainException.class, 
        () -> useCase.handle(command)).getViolations();

    assertThat(violations).isNotEmpty();
    assertThat(violations).containsEntry("descricao", "O comentário é obrigatório");
  }

}
