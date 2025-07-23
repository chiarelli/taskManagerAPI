package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.event.StatusTarefaAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAlteradaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaExcluidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.ConstraintViolation;

public class TarefaTest {

  private Projeto projeto;
  private Tarefa tarefa;
  private Historico historico;

  @BeforeEach
  void setUp() {
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

    historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Status",
      "Alterado de PENDENTE para EM_ANDAMENTO",
      new AutorId("123")
    );

    projeto = new Projeto("Projeto teste", "Um projeto apenas para testes.");
  }

  @Test
  void deveAlterarStatusComSucessoEEmitirEvento() {

    tarefa.alterarStatus(projeto, eStatusTarefaVO.EM_ANDAMENTO, historico);

    assertThat(tarefa.getStatus()).isEqualTo(eStatusTarefaVO.EM_ANDAMENTO);
    assertThat(tarefa.getHistoricos()).contains(historico.getId());
    assertThat(tarefa.flushEvents()).anyMatch(e -> e instanceof StatusTarefaAlteradoEvent);
  }

  @Test
  void deveLancarExcecaoSeStatusNaoMudar() {

    assertThat(tarefa.flushEvents()).noneMatch(e -> e instanceof StatusTarefaAlteradoEvent);

    assertThatThrownBy(() -> tarefa.alterarStatus(projeto, eStatusTarefaVO.PENDENTE, historico))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Status já se encontra");
  }

  @Test
  void deveAlterarDescricaoComSucessoEEmitirEvento() {
    var novaDescricao = "Nova descrição";

    tarefa.alterarDescricao(projeto, novaDescricao, historico);

    assertThat(tarefa.getDescricao()).isEqualTo(novaDescricao);
    assertThat(tarefa.getHistoricos()).contains(historico.getId());
    assertThat(tarefa.flushEvents()).anyMatch(e -> e instanceof TarefaAlteradaEvent);
  }

  @Test
  void naoDeveEmitirEventoSeDescricaoNaoMudar() {
    tarefa.alterarDescricao(projeto, "Descrição", historico);

    assertThat(tarefa.flushEvents()).isEmpty();
  }

  @Test
  void deveAdicionarComentarioComSucessoEEmitirEvento() {
    var comentario = mock(Comentario.class);

    tarefa.adicionarComentario(projeto, comentario, historico);

    assertThat(tarefa.getComentarios()).contains(comentario.getId());
    assertThat(tarefa.getHistoricos()).contains(historico.getId());
    assertThat(tarefa.flushEvents()).anyMatch(e -> e instanceof ComentarioAdicionadoEvent);
  }

  @Test
  void deveLancarExcecaoExcluirTarefaStatusPendente() {
    assertThatThrownBy(() -> tarefa.excluirTarefa(projeto) )
      .isInstanceOf(DomainException.class)
     .hasMessageContaining("Tarefa com status pendente não pode ser excluida");

    assertThat(tarefa.flushEvents()).noneMatch(e -> e instanceof TarefaExcluidaEvent);
  }

   @Test
   void deveExcluirTarefaNaoPendente() {
     tarefa.alterarStatus(projeto, eStatusTarefaVO.CONCLUIDA, historico);

     tarefa.excluirTarefa(projeto);
     
     assertThat(tarefa.flushEvents()).anyMatch(e -> e instanceof TarefaExcluidaEvent);
   }

  @Test
  void deveValidarTarefaValidaSemErros() {

    var tarefa = new Tarefa(
      "Titulo da tarefa",
      "Descrição",
      DataVencimentoVO.of(OffsetDateTime.now().plusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA
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
      null
    );

    GenericValidator<Tarefa> validator = new GenericValidator<>(tarefa);

    DomainException ex = assertThrows(DomainException.class, validator::assertValid);

    assertNotNull(ex.getViolations());
    assertEquals(4, ex.getViolations().size());

    // Opcional: checar mensagens ou paths específicos
    var paths = ex.getViolations().keySet() 
      .stream()
      .toList();

      assertTrue(paths.contains("prioridade"));
      assertTrue(paths.contains("dataVencimento"));
      assertTrue(paths.contains("titulo"));
      assertTrue(paths.contains("status"));
  }

  @Test
  void deveLancarExcecaoQuandoTarefaInvalidaAtributosInvalidos() {

    var tarefa = new Tarefa(
      "Titulo",
      "Descrição",
      DataVencimentoVO.of(OffsetDateTime.now().minusDays(1)),
      eStatusTarefaVO.PENDENTE,
      ePrioridadeVO.BAIXA
    );

    GenericValidator<Tarefa> validator = new GenericValidator<>(tarefa);

    DomainException ex = assertThrows(DomainException.class, validator::assertValid);

    assertEquals(2, ex.getViolations().size());

    assertEquals("O titulo deve ter entre 3 e 100 caracteres", ex.getViolations().get("titulo"));
    assertEquals("A data de vencimento deve ser maior que a data atual", ex.getViolations().get("dataVencimento.dataVencimento"));
  }
  
}
