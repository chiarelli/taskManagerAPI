package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaRemovidaEvent;
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
        new DataVencimentoVO(LocalDateTime.now().plusDays(1)),
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
  }

  @Test
  void deveAdicionarTarefaComSucesso() {
    Tarefa tarefa = new Tarefa(
        new TarefaId(),
        "Tarefa 1",
        "Descrição",
        new DataVencimentoVO(LocalDateTime.now().plusDays(1)),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.BAIXA,
        0L,
        new HashSet<>(),
        new HashSet<>());

    projeto.adicionarTarefa(tarefa);

    assertTrue(projeto.getTarefas().contains(tarefa));
    assertThat(projeto.dumpEvents()).anyMatch(e -> e instanceof TarefaAdicionadaEvent);
  }

  @Test
  void naoDeveAdicionarMaisQue20Tarefas() {
    for (int i = 0; i < 20; i++) {
      projeto.adicionarTarefa(new Tarefa(new TarefaId(), "T" + i, "D", new DataVencimentoVO(LocalDateTime.now()),
          eStatusTarefaVO.PENDENTE, ePrioridadeVO.MEDIA, 0L, new HashSet<>(), new HashSet<>()));
    }

    Tarefa tarefaExtra = new Tarefa(new TarefaId(), "T21", "D", new DataVencimentoVO(LocalDateTime.now()),
        eStatusTarefaVO.PENDENTE, ePrioridadeVO.MEDIA, 0L, new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> projeto.adicionarTarefa(tarefaExtra))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Limite de tarefas");

    assertThat(projeto.dumpEvents()).hasSize(20);
  }

  @Test
  void deveRemoverTarefaComSucesso() {
    Tarefa tarefa = new Tarefa(new TarefaId(), "T1", "D", new DataVencimentoVO(LocalDateTime.now()),
        eStatusTarefaVO.PENDENTE, ePrioridadeVO.ALTA, 0L, new HashSet<>(), new HashSet<>());
    projeto.adicionarTarefa(tarefa);

    assertThat(projeto.dumpEvents())
      .filteredOn(e -> e instanceof TarefaAdicionadaEvent)
      .anyMatch(e -> e instanceof TarefaAdicionadaEvent)
      .hasSize(1);

    projeto.removerTarefa(tarefa.getId());

    assertThat(projeto.dumpEvents())
      .filteredOn(e -> e instanceof TarefaRemovidaEvent)
      .anyMatch(e -> e instanceof TarefaRemovidaEvent)
      .hasSize(1);

    assertFalse(projeto.getTarefas().contains(tarefa));
  }

  @Test
  void devePermitirRemoverProjetoSemTarefasPendentes() {
    Tarefa tarefa = new Tarefa(new TarefaId(), "Tarefa", "D", new DataVencimentoVO(LocalDateTime.now()),
        eStatusTarefaVO.CONCLUIDA, ePrioridadeVO.BAIXA, 0L, new HashSet<>(), new HashSet<>());
    projeto.adicionarTarefa(tarefa);

    assertThatCode(projeto::excluirProjeto).doesNotThrowAnyException();
  }

  @Test
  void deveLancarExcecaoAoRemoverTarefaInexistente() {
    TarefaId inexistente = new TarefaId();

    assertThatThrownBy(() -> projeto.removerTarefa(inexistente))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("não pertence ao projeto");
  }


}
