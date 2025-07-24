package com.github.chiarelli.taskmanager.application.usecases.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;

public class CriarEAdicionarComentarioCommandTest {

  ProjetoId projetoId = new ProjetoId();
  TarefaId tarefaId = new TarefaId();
  AutorId autorId = new AutorId("123");

  String tituloValido = "Título válido";
  String descricaoValida = "Descrição válida do comentário";

  @Test
  void deveSerValidoComDadosCorretos() {
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        tarefaId,
        tituloValido,
        descricaoValida,
        autorId
    );

    assertDoesNotThrow(command::validate);
  }

  @Test
  void deveFalharSeTituloForMuitoCurto() {
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        tarefaId,
        "Curto",
        descricaoValida,
        autorId
    );

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("titulo"));
    assertThat(ex.getViolations().get("titulo")).isEqualTo("O título deve ter entre 8 e 100 caracteres");
  }

  @Test
  void deveFalharSeTituloForMuitoLongo() {
    var tituloLongo = "T".repeat(101);
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        tarefaId,
        tituloLongo,
        descricaoValida,
        autorId
    );

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("titulo"));
    assertThat(ex.getViolations().get("titulo")).isEqualTo("O título deve ter entre 8 e 100 caracteres");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = { "   " })
  void deveFalharSeDescricaoForNulaOuEmBranco(String descricaoInvalida) {
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        tarefaId,
        tituloValido,
        descricaoInvalida,
        autorId);

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("descricao"));
    assertThat(ex.getViolations().get("descricao")).isEqualTo("O comentário é obrigatório");
  }
  
  @Test
  void deveFalharSeDescricaoForMuitoLonga() {
    var descricaoLonga = "D".repeat(256);
    var command = new CriarEAdicionarComentarioCommand(
      projetoId,
      tarefaId,
      tituloValido,
      descricaoLonga,
      autorId
      );
      
    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("descricao"));
    assertThat(ex.getViolations().get("descricao")).isEqualTo("A descrição não pode ter mais de 255 caracteres");
      
    
  }

  @Test
  void deveFalharSeAutorForNull() {
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        tarefaId,
        tituloValido,
        descricaoValida,
        null
    );

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("autor"));
    assertThat(ex.getViolations().get("autor")).isEqualTo("não deve ser nulo");
  }

  @Test
  void deveFalharSeProjetoIdForNull() {
    var command = new CriarEAdicionarComentarioCommand(
        null,
        tarefaId,
        tituloValido,
        descricaoValida,
        autorId
    );

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("projetoId"));
    assertThat(ex.getViolations().get("projetoId")).isEqualTo("não deve ser nulo");
  }
  
  @Test
  void deveFalharSeTarefaIdForNull() {
    var command = new CriarEAdicionarComentarioCommand(
        projetoId,
        null,
        tituloValido,
        descricaoValida,
        autorId
    );

    var ex = assertThrows(DomainException.class, command::validate);
    assertTrue(ex.getViolations().containsKey("tarefaId"));
    assertThat(ex.getViolations().get("tarefaId")).isEqualTo("não deve ser nulo");
  }

}
