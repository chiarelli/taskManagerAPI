package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.DescricaoTarefaAlteradaEvent;
import com.github.chiarelli.taskmanager.domain.event.StatusTarefaAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class TarefaTest {

  private Tarefa tarefa;

  @BeforeEach
  void setUp() {
    tarefa = new Tarefa(
      new TarefaId(),
      "Titulo",
      "Descrição",
      new DataVencimentoVO(LocalDateTime.now().plusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA,
      0L,
      new HashSet<>(),
      new HashSet<>()
    );
  }

  @Test
  void deveAlterarStatusComSucessoEEmitirEvento() {
    var historicoId = new HistoricoId();

    tarefa.alterarStatus(eStatusTarefaVO.EM_ANDAMENTO, historicoId);

    assertThat(tarefa.getStatus()).isEqualTo(eStatusTarefaVO.EM_ANDAMENTO);
    assertThat(tarefa.getHistoricos()).contains(historicoId);
    assertThat(tarefa.dumpEvents()).anyMatch(e -> e instanceof StatusTarefaAlteradoEvent);
    // Deve incrementar a versão
    assertThat(tarefa.getVersion()).isEqualTo(1L);
  }

  @Test
  void deveLancarExcecaoSeStatusNaoMudar() {
    var historicoId = new HistoricoId();

    assertThatThrownBy(() -> tarefa.alterarStatus(eStatusTarefaVO.PENDENTE, historicoId))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Status já se encontra");
    // Versão deve manter 0
    assertThat(tarefa.getVersion()).isEqualTo(0L);
  }

  @Test
  void deveAlterarDescricaoComSucessoEEmitirEvento() {
    var historicoId = new HistoricoId();
    var novaDescricao = "Nova descrição";

    tarefa.alterarDescricao(novaDescricao, historicoId);

    assertThat(tarefa.getDescricao()).isEqualTo(novaDescricao);
    assertThat(tarefa.getHistoricos()).contains(historicoId);
    assertThat(tarefa.dumpEvents()).anyMatch(e -> e instanceof DescricaoTarefaAlteradaEvent);
    // Deve incrementar a versão
    assertThat(tarefa.getVersion()).isEqualTo(1L);
  }

  @Test
  void naoDeveEmitirEventoSeDescricaoNaoMudar() {
    var historicoId = new HistoricoId();
    tarefa.alterarDescricao("Descrição", historicoId);

    assertThat(tarefa.dumpEvents()).isEmpty();
    // Versão deve manter 0
    assertThat(tarefa.getVersion()).isEqualTo(0L);
  }

  @Test
  void deveAdicionarComentarioComSucessoEEmitirEvento() {
    var comentarioId = new ComentarioId();
    var historicoId = new HistoricoId();

    tarefa.adicionarComentario(comentarioId, historicoId);

    assertThat(tarefa.getComentarios()).contains(comentarioId);
    assertThat(tarefa.getHistoricos()).contains(historicoId);
    assertThat(tarefa.dumpEvents()).anyMatch(e -> e instanceof ComentarioAdicionadoEvent);
    // Deve incrementar a versão
    assertThat(tarefa.getVersion()).isEqualTo(1L);
  }

   @Test
   void devePermitirExclusaoSeStatusPendente() {
     tarefa.excluirTarefa();

     assertThat(tarefa.dumpEvents()).anyMatch(e -> e instanceof TarefaExcluidaEvent);
   }

   @Test
   void deveLancarExcecaoAoExcluirTarefaNaoPendente() {
     tarefa.alterarStatus(eStatusTarefaVO.CONCLUIDA, new HistoricoId());
      // Versao deve alterar
     assertThat(tarefa.getVersion()).isEqualTo(1L);
     
     assertThatThrownBy(() -> tarefa.excluirTarefa())
     .isInstanceOf(DomainException.class)
     .hasMessageContaining("Tarefa com status diferente de pendente");
     // Versão deve manter 1
     assertThat(tarefa.getVersion()).isEqualTo(1L);
   }
}
