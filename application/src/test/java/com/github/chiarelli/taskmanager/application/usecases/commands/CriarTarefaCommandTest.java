package com.github.chiarelli.taskmanager.application.usecases.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public class CriarTarefaCommandTest {

  @Test
  void deveSerValidoQuandoTodosOsCamposForemValidos() {
    var command = new CriarTarefaCommand(
      new ProjetoId(),
      "Título válido",
      "Descrição válida",
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    assertDoesNotThrow(command::validate);
  }

  @Test
  void deveDispararExcecaoQuandoTituloForMuitoCurto() {
    var command = new CriarTarefaCommand(
      new ProjetoId(),
      "Curto", // < 8 caracteres
      "Descrição válida",
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().values()).contains("O título deve ter entre 8 e 100 caracteres");
  }

  @Test
  void deveDispararExcecaoQuandoDescricaoForMuitoLonga() {
    var descricao = "A".repeat(256); // > 255
    var command = new CriarTarefaCommand(
      new ProjetoId(),
      "Título válido",
      descricao,
      DataVencimentoVO.now(),
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().values()).contains("A descricao nao pode ter mais de 255 caracteres");
  }

  @Test
  void deveDispararExcecaoQuandoDataVencimentoForNula() {
    var command = new CriarTarefaCommand(
      new ProjetoId(),
      "Título válido",
      "Descrição válida",
      null, // Data nula
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().values()).contains("A data de vencimento deve ser informada");
  }

  @Test
  void deveDispararExcecaoQuandoDataVencimentoForMuitoAntiga() {
    var dataAntiga = Date.from(Instant.now().minus(Duration.ofHours(2)));

    var vencimento = new DataVencimentoVO(dataAntiga);

    var command = new CriarTarefaCommand(
      new ProjetoId(),
      "Título válido",
      "Descrição válida",
      vencimento,
      ePrioridadeVO.MEDIA,
      eStatusTarefaVO.PENDENTE
    );

    var ex = assertThrows(DomainException.class, command::validate);

    assertThat(ex.getViolations().values()).contains("A data de vencimento deve ser maior que a data atual");
  }

}
