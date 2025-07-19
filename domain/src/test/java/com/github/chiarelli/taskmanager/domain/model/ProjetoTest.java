package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ProjetoExcluidoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class ProjetoTest {

  private Projeto projeto;

  @BeforeEach
  void setUp() {
    projeto = new Projeto(
      new ProjetoId(),
      "Projeto X", 
      "Descrição", 
      0L, 
      new HashSet<>());
  }

  @Test
  void naoDevePermitirRemocaoDeProjetoComTarefaPendente() {
    Tarefa tarefaPendente = new Tarefa(
        new TarefaId(),
        "Tarefa 1",
        "Descrição",
        DataVencimentoVO.of(OffsetDateTime.now().plusDays(1)),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.MEDIA,
        0L,
        new HashSet<>(),
        new HashSet<>()
    );

    projeto.adicionarTarefa(tarefaPendente);

    assertThatThrownBy(projeto::excluirProjeto)
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("tarefas pendentes");

    assertThat(projeto.flushEvents()).noneMatch(e -> e instanceof TarefaExcluidaEvent);
    assertThat(projeto.flushEvents()).noneMatch(e -> e instanceof ProjetoExcluidoEvent);
  }

  @Test
  void deveAdicionarTarefaComSucesso() {
    Tarefa tarefa = new Tarefa(
        new TarefaId(),
        "Tarefa 1",
        "Descrição",
        DataVencimentoVO.of(OffsetDateTime.now().plusDays(1)),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.BAIXA,
        0L,
        new HashSet<>(),
        new HashSet<>());

    projeto.adicionarTarefa(tarefa);

    assertTrue(projeto.getTarefas().contains(tarefa));
    assertThat(projeto.flushEvents()).anyMatch(e -> e instanceof TarefaAdicionadaEvent);
  }

  @Test
  void naoDeveAdicionarMaisQue20Tarefas() {
    for (int i = 0; i < 20; i++) {
      projeto.adicionarTarefa(new Tarefa(new TarefaId(), "T" + i, "D", DataVencimentoVO.of(OffsetDateTime.now()),
          eStatusTarefaVO.PENDENTE, ePrioridadeVO.MEDIA, 0L, new HashSet<>(), new HashSet<>()));
    }

    Tarefa tarefaExtra = new Tarefa(new TarefaId(), "T21", "D", DataVencimentoVO.of(OffsetDateTime.now()),
        eStatusTarefaVO.PENDENTE, ePrioridadeVO.MEDIA, 0L, new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> projeto.adicionarTarefa(tarefaExtra))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Limite de tarefas");

    assertThat(projeto.flushEvents())
      .hasSize(20)
      .anyMatch(e -> e instanceof TarefaAdicionadaEvent);
  }

  @Test
  void naoDeveRemoverTarefaPendente() {
    Tarefa tarefa = new Tarefa(new TarefaId(), "T1", "D", DataVencimentoVO.of(OffsetDateTime.now()),
        eStatusTarefaVO.PENDENTE, ePrioridadeVO.ALTA, 0L, new HashSet<>(), new HashSet<>());
    projeto.adicionarTarefa(tarefa);

    assertThat(projeto.flushEvents())
      .anyMatch(e -> e instanceof TarefaAdicionadaEvent)
      .hasSize(1);

    assertThatThrownBy(() -> projeto.removerTarefa(tarefa.getId()))
      .isInstanceOf(DomainException.class);
    
  }

  @Test
  void devePermitirRemoverProjetoSemTarefasPendentes() {
    Tarefa tarefa1 = new Tarefa(new TarefaId(), "Tarefa", "D", DataVencimentoVO.of(OffsetDateTime.now()),
        eStatusTarefaVO.CONCLUIDA, ePrioridadeVO.BAIXA, 0L, new HashSet<>(), new HashSet<>());
    projeto.adicionarTarefa(tarefa1);

    Tarefa tarefa2 = new Tarefa(new TarefaId(), "Tarefa", "D", DataVencimentoVO.of(OffsetDateTime.now()),
        eStatusTarefaVO.EM_ANDAMENTO, ePrioridadeVO.ALTA, 0L, new HashSet<>(), new HashSet<>());
    projeto.adicionarTarefa(tarefa2);

    assertThatCode(projeto::excluirProjeto).doesNotThrowAnyException();

    var projectEvents = projeto.flushEvents();
    assertThat(projectEvents)
      .filteredOn(e -> e instanceof TarefaAdicionadaEvent)
      .anyMatch(e -> e instanceof TarefaAdicionadaEvent)
      .hasSize(2);

    assertThat(projectEvents)
      .filteredOn(e -> e instanceof ProjetoExcluidoEvent)
      .anyMatch(e -> e instanceof ProjetoExcluidoEvent)
      .hasSize(1);

    assertThat( Stream.concat(tarefa1.flushEvents().stream(), tarefa2.flushEvents().stream()) )
      .filteredOn(e -> e instanceof TarefaExcluidaEvent)
      .anyMatch(e -> e instanceof TarefaExcluidaEvent)
      .hasSize(2);

    assertFalse(projeto.getTarefas().contains(tarefa1));
  }

  @Test
  void deveLancarExcecaoAoRemoverTarefaInexistente() {
    TarefaId inexistente = new TarefaId();

    assertThatThrownBy(() -> projeto.removerTarefa(inexistente))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("não pertence ao projeto");

    assertThat(projeto.flushEvents()).isEmpty();
  }


}
