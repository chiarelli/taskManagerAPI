package com.github.chiarelli.taskmanager.application.usecases.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class AlterarStatusTarefaCommandTest {

  private ProjetoId projetoId;
  private TarefaId tarefaId;

  @BeforeEach
  void setUp() {
    projetoId = new ProjetoId(UUID.randomUUID());
    tarefaId = new TarefaId(UUID.randomUUID());
  }

  @Test
  void deveLancarExcecaoQuandoProjetoIdForNulo() {
    var command = new AlterarStatusTarefaCommand(
        null,
        tarefaId,
        eStatusTarefaVO.EM_ANDAMENTO,
        1L
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().get("projetoId")).isEqualTo("não deve ser nulo");
  }

  @Test
  void deveLancarExcecaoQuandoTarefaIdForNulo() {
    var command = new AlterarStatusTarefaCommand(
        projetoId,
        null,
        eStatusTarefaVO.EM_ANDAMENTO,
        1L
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().get("tarefaId")).isEqualTo("não deve ser nulo");
  }

  @Test
  void deveLancarExcecaoQuandoStatusForNulo() {
    var command = new AlterarStatusTarefaCommand(
        projetoId,
        tarefaId,
        null,
        1L
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().get("status")).isEqualTo("não deve ser nulo");
  }

  @Test
  void deveLancarExcecaoQuandoVersaoForNegativa() {
    var command = new AlterarStatusTarefaCommand(
        projetoId,
        tarefaId,
        eStatusTarefaVO.EM_ANDAMENTO,
        -1L
    );

    var ex = assertThrows(DomainException.class, command::validate); 

    assertThat(ex.getViolations().get("version")).isEqualTo("A versão não pode ser negativa");
  }

  @Test
  void deveSerValidoQuandoTodosOsDadosForemCorretos() {
    var command = new AlterarStatusTarefaCommand(
        projetoId,
        tarefaId,
        eStatusTarefaVO.EM_ANDAMENTO,
        0L
    );

    // não deve lançar exceção
    command.validate();
  }

}
