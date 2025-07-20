package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.event.HistoricoAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.StatusTarefaAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAlteradaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class TarefaServiceTest {

  @Mock
  iTarefasRepository tarefaRepository;
  
  @Mock
  iProjetoRepository projetoRepository;
  
  @InjectMocks
  DomainEventBufferImpl eventBuffer;

  TarefaService tarefaService;

  private Projeto projeto;
  private Tarefa tarefa;

  @BeforeEach
  void setUp() {
    projeto = new Projeto(
      new ProjetoId(),
      "Projeto X", 
      "Descrição", 
      0L, 
      new HashSet<>());

    tarefa = new Tarefa(
      new TarefaId(),
      "Titulo",
      "Descrição",
      DataVencimentoVO.of(OffsetDateTime.now().plusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA,
      new HashSet<>(),
      new HashSet<>()
    );

    tarefaService = new TarefaService(tarefaRepository, projetoRepository, eventBuffer);
  }

  @Test
  void deveAlterarStatusComSucessoEChamarRepositorios() {
    // var tarefa = mock(Tarefa.class);
    var randomUUID = UUID.randomUUID();
    var autorId = new AutorId(randomUUID.toString());
    var novoStatus = eStatusTarefaVO.CONCLUIDA;

      // Adicionar a tarefa ao projeto
    projeto.adicionarTarefa(tarefa);
    projeto.flushEvents(); // Limpa os eventos de domínio

    // Simula uma consulta no banco de dados
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

    var data = new AlterarTarefa(projeto.getId(), tarefa.getId(), 
      tarefa.getTitulo(), tarefa.getDescricao(), tarefa.getDataVencimento(), 
      novoStatus, tarefa.getPrioridade());

    List<AbstractDomainEvent<?>> events = tarefaService.alterarStatusComHistorico(data, autorId).events();

    // Assert
    assertTrue(events.size() == 2, "Deve ter emitido 2 eventos");
    assertThat(events).anyMatch(e -> e instanceof HistoricoAdicionadoEvent, "Deve ter emitido um evento do tipo HistoricoAdicionadoEvent");
    assertThat(events).anyMatch(e -> e instanceof StatusTarefaAlteradoEvent, "Deve ter emitido um evento do tipo StatusTarefaAlteradoEvent");

    assertThat(tarefa.getStatus())
      .as("Status deve ter sido alterado")
      .isEqualTo(novoStatus);

    verify(tarefaRepository).saveHistorico(eq(tarefa.getId()), any());
  }

  @Test
  void deveAlterarDescricaoComSucessoEChamarRepositorios() {
    var autor = new AutorId(UUID.randomUUID().toString());
    var novaDescricao = "Nova descrição";

     // Adicionar a tarefa ao projeto
    projeto.adicionarTarefa(tarefa);
    projeto.flushEvents(); // Limpa os eventos de domínio

    // Simula uma consulta no banco de dados
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

    var data = new AlterarTarefa(projeto.getId(), tarefa.getId(), 
      tarefa.getTitulo(), novaDescricao, tarefa.getDataVencimento(), 
      tarefa.getStatus(), tarefa.getPrioridade());

    List<AbstractDomainEvent<?>> events = tarefaService.alterarDescricaoComHistorico(data, autor).events();

    // Assert
    assertTrue(events.size() == 2, "Deve ter emitido 2 evento");
    assertThat(events).anyMatch(e -> e instanceof HistoricoAdicionadoEvent, "Deve ter emitido um evento do tipo HistoricoAdicionadoEvent");
    assertThat(events).anyMatch(e -> e instanceof TarefaAlteradaEvent, "Deve ter emitido um evento do tipo TarefaAlteradaEvent");
    
    assertThat(tarefa.getDescricao())
      .as("A descrição deve ter sido alterada")
      .contains(novaDescricao);

    verify(tarefaRepository).saveHistorico(eq(tarefa.getId()), any());
  }

  @Test
  void deveAdicionarComentarioEChamarRepositorios() {
      var tarefa = mock(Tarefa.class);
      var comentario = new Comentario(
          new ComentarioId(),
          LocalDateTime.now(),
          "comentário teste",
          "Descrição teste",
          new AutorId(UUID.randomUUID().toString())
      );

      projeto.adicionarTarefa(tarefa);
      projeto.flushEvents(); // Limpa os eventos de domínio

      when(tarefa.getId()).thenReturn(new TarefaId());
      when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

      tarefaService.adicionarComentarioComHistorico(projeto.getId(), tarefa.getId(), comentario);

      verify(tarefa).adicionarComentario(eq(projeto), eq(comentario), any());
      verify(tarefaRepository).saveComentario(eq(tarefa.getId()), eq(comentario));
      verify(tarefaRepository).saveHistorico(eq(tarefa.getId()), any());
  }

  @Test
  void deveEmitirEventosAoAdicionarComentario() {
    var comentario = new Comentario(
          new ComentarioId(),
          LocalDateTime.now(),
          "comentário teste",
          "Descrição teste",
          new AutorId(UUID.randomUUID().toString())
      );

      projeto.adicionarTarefa(tarefa);
      projeto.flushEvents(); // Limpa os eventos de domínio

      when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

      List<AbstractDomainEvent<?>> events 
          = tarefaService
              .adicionarComentarioComHistorico(projeto.getId(), tarefa.getId(), comentario)
              .events();

      assertThat(events).anyMatch(e -> e instanceof ComentarioAdicionadoEvent, "Deve ter emitido um evento do tipo ComentarioAdicionadoEvent");
      assertThat(events).anyMatch(e -> e instanceof HistoricoAdicionadoEvent, "Deve ter emitido um evento do tipo HistoricoAdicionadoEvent");
  }

  @Test
  void deveExcluirTarefaComStatusDiferenteDePendenteEChamarRepositorios() {
    var autor = new AutorId(UUID.randomUUID().toString());
    var historico = mock(Historico.class);

    tarefa.alterarStatus(projeto, eStatusTarefaVO.CONCLUIDA, historico);
    tarefa.flushEvents(); // Limpa os eventos de domínio

    // Adicionar a tarefa ao projeto
    projeto.adicionarTarefa(tarefa);
    projeto.flushEvents(); // Limpa os eventos de domínio

    // Simula uma consulta no banco de dados
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

    var data = new ExcluirTarefa(projeto.getId(), tarefa.getId());

    List<AbstractDomainEvent<?>> events 
        = tarefaService.excluirTarefaComHistorico(data, autor).events();

    // Assert
    assertThat(events).anyMatch(e -> e instanceof TarefaExcluidaEvent, "Deve ter emitido um evento do tipo TarefaExcluidaEvent");
    assertThat(events).anyMatch(e -> e instanceof HistoricoAdicionadoEvent, "Deve ter emitido um evento do tipo HistoricoAdicionadoEvent");

    assertThat(projeto.getTarefas())
      .as("a tarefa deve ser removida do projeto após exclusão")
      .doesNotContain(tarefa);

    verify(projetoRepository).deleteTarefa(projeto.getId(), tarefa.getId());
    verify(tarefaRepository).saveHistorico(eq(tarefa.getId()), any());
  }

  @Test
  void devePropagarExcecaoAoExcluirTarefaComStatusInvalido() {
    // Arrange
    AutorId autorId = new AutorId(UUID.randomUUID().toString());
    List<AbstractDomainEvent<?>> events = new ArrayList<>();

    // Adicionar a tarefa ao projeto
    projeto.adicionarTarefa(tarefa);

    // Simula uma consulta no banco de dados
    when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

    // Assert - a exclusão deve lançar a exceção
    assertThatThrownBy(() -> {
        var data = new ExcluirTarefa(projeto.getId(), tarefa.getId());
        var Devents = tarefaService.excluirTarefaComHistorico(data, autorId).events();
        Devents.forEach(events::add);
    })
    .isInstanceOf(DomainException.class)
    .hasMessage("Tarefa com status pendente não pode ser excluida.");

    // Assert
    assertThat(events).noneMatch(e -> e instanceof TarefaExcluidaEvent, "Não deve ter emitido um evento do tipo TarefaExcluidaEvent");
    assertThat(events).noneMatch(e -> e instanceof HistoricoAdicionadoEvent, "Nao deve ter emitido um evento do tipo HistoricoAdicionadoEvent");

    // Verifica que a tarefa *ainda está presente* no projeto
    assertThat(projeto.getTarefas())
      .as("A tarefa não deveria ter sido removida do projeto")
      .contains(tarefa);

    verify(projetoRepository, never()).deleteTarefa(projeto.getId(), tarefa.getId());
    verify(tarefaRepository, never()).saveHistorico(eq(tarefa.getId()), any());
  }

}
