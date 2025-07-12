package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.ConstraintViolation;

public class TarefaTest {

  private Tarefa tarefa;

  @BeforeEach
  void setUp() {
    tarefa = new Tarefa(
      new TarefaId(),
      "Titulo",
      "Descrição",
      DataVencimentoVO.of(LocalDateTime.now().plusDays(1)),
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

  @Test
  void deveValidarTarefaValidaSemErros() {

    var tarefa = new Tarefa(
      "Titulo da tarefa",
      "Descrição",
      DataVencimentoVO.of(LocalDateTime.now().plusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA,
      0L
    );

    GenericValidator<Tarefa> validator = new GenericValidator<>(tarefa);
    Set<ConstraintViolation<Tarefa>> violations = validator.validate();

    assertTrue(violations.isEmpty(), "A tarefa deveria ser válida");
  }

  @Test
  void deveLancarExcecaoQuandoTarefaInvalidaAtributosEmBranco() {

    var tarefa = new Tarefa(
      "",
      "",
      null,
      null,
      null,
      null
    );

    GenericValidator<Tarefa> validator = new GenericValidator<>(tarefa);

    DomainException ex = assertThrows(DomainException.class, validator::assertValid);

    assertNotNull(ex.getViolations());
    assertEquals(5, ex.getViolations().size());

    // // Opcional: checar mensagens ou paths específicos
    var paths = ex.getViolations().keySet() 
      .stream()
      .toList();

      assertTrue(paths.contains("prioridade"));
      assertTrue(paths.contains("dataVencimento"));
      assertTrue(paths.contains("titulo"));
      assertTrue(paths.contains("version"));
      assertTrue(paths.contains("status"));
  }

  @Test
  void deveLancarExcecaoQuandoTarefaInvalidaAtributosInvalidos() {

    var tarefa = new Tarefa(
      "Titulo",
      "Descrição",
      DataVencimentoVO.of(LocalDateTime.now().minusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA,
      -1L
    );

    GenericValidator<Tarefa> validator = new GenericValidator<>(tarefa);

    DomainException ex = assertThrows(DomainException.class, validator::assertValid);

    assertEquals(3, ex.getViolations().size());

    assertEquals("O titulo deve ter entre 3 e 100 caracteres", ex.getViolations().get("titulo"));
    assertEquals("deve ser maior que ou igual à 0", ex.getViolations().get("version"));
    assertEquals("A data de vencimento deve ser maior que a data atual", ex.getViolations().get("dataVencimento.dataVencimento"));
  }
  
}
