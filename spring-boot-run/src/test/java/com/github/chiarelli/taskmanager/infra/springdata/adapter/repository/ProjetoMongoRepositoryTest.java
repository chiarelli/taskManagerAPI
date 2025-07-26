package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;
import com.github.chiarelli.taskmanager.spring_boot_run.TaskManagerSpringBootRunApplication;

@SpringBootTest(classes = TaskManagerSpringBootRunApplication.class)
@Import(ProjetoMongoRepository.class)
public class ProjetoMongoRepositoryTest extends MongoTestContainer {

  @Autowired ProjetoMongoRepository projetoRepo;

  @Test
  void deveSalvarERecuperarProjeto() {
    // Arrange
    var projetoId = new ProjetoId(UUID.randomUUID());
    Projeto projeto = new Projeto(
        projetoId,
        "Projeto Teste",
        "Descrição do projeto",
        0L,
        java.util.Collections.emptySet()
    );

    // Act
    projetoRepo.save(projeto);

    Optional<Projeto> resultado = projetoRepo.findById(projetoId);

    // Assert
    assertThat(resultado).isPresent();
    assertThat(resultado.get().getTitulo()).isEqualTo("Projeto Teste");
  }

  @Test
  void deveSalvarProjetoComTarefas() {
    // Arrange
    LocalDate localDatePlusOne = LocalDate.now().plusDays(1);

    Date datePlusOne = Date.from(
          localDatePlusOne.atStartOfDay(ZoneId.systemDefault()).toInstant());

    var projetoId = new ProjetoId(UUID.randomUUID());
    var tarefa = new Tarefa(
        new TarefaId(UUID.randomUUID()),
        "Tarefa Teste",
        "Descrição",
        new DataVencimentoVO(datePlusOne),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.MEDIA,
        Set.of(),
        Set.of());

    var projeto = new Projeto(
        projetoId,
        "Projeto com Tarefa",
        "Descrição do projeto",
        0L,
        Set.of(tarefa));

    // Act
    projetoRepo.save(projeto);

    // Assert
    var recuperado = projetoRepo.findById(projetoId);
    assertThat(recuperado).isPresent();
    assertThat(recuperado.get().getTarefas()).hasSize(1);
    var tarefaRecuperada = recuperado.get().getTarefas().iterator().next();
    assertThat(tarefaRecuperada.getTitulo()).isEqualTo("Tarefa Teste");
  }

  @Test
  void deveAtualizarTarefaDentroDoProjeto() {
    var projetoId = new ProjetoId(UUID.randomUUID());
    var tarefaId = new TarefaId(UUID.randomUUID());

    var tarefa = new Tarefa(
        tarefaId,
        "Título Original",
        "Descrição",
        DataVencimentoVO.now(),
        eStatusTarefaVO.CONCLUIDA,
        ePrioridadeVO.ALTA,
        Set.of(),
        Set.of());

    var projeto = new Projeto(projetoId, "Projeto", "Desc", 0L, Set.of(tarefa));
    projetoRepo.save(projeto);

    // Modifica a tarefa
    projeto = projetoRepo.findById(projetoId).get();
    var tarefaModificada = new Tarefa(
        tarefaId,
        "Título Alterado",
        "Descrição",
        DataVencimentoVO.now(),
        eStatusTarefaVO.CONCLUIDA,
        ePrioridadeVO.BAIXA,
        Set.of(),
        Set.of());

    assertThat(projeto.getTarefas()).hasSize(1);
    assertThat(projeto.getTarefas()).anyMatch(t -> t.getId().equals(tarefaId));
    
    projeto.removerTarefa(tarefaId);
    projetoRepo.save(projeto);

    assertThat(projeto.getTarefas()).noneMatch(t -> t.getId().equals(tarefaId));

    projeto.adicionarTarefa(tarefaModificada);
    projetoRepo.save(projeto);
    
    assertThat(projeto.getTarefas()).hasSize(1);

    // Assert
    var recuperado = projetoRepo.findById(projetoId);
    assertThat(recuperado).isPresent();
    var tarefaAtual = recuperado.get().getTarefas().stream().findFirst().orElseThrow();
    assertThat(tarefaAtual.getTitulo()).isEqualTo("Título Alterado");
    assertThat(tarefaAtual.getStatus()).isEqualTo(eStatusTarefaVO.CONCLUIDA);
  }

  @Test
  void deveDeletarTarefaDoProjeto() {
    var projetoId = new ProjetoId(UUID.randomUUID());
    var tarefaId = new TarefaId(UUID.randomUUID());

    var tarefa = new Tarefa(
        tarefaId,
        "Tarefa",
        "Desc",
        DataVencimentoVO.now(),
        eStatusTarefaVO.CONCLUIDA,
        ePrioridadeVO.MEDIA,
        Set.of(),
        Set.of());

    var projeto = new Projeto(projetoId, "Projeto", "Desc", 0L, Set.of(tarefa));
    projetoRepo.save(projeto);

    // Act
    projetoRepo.deleteTarefa(projetoId, tarefaId);

    // Assert
    var atualizado = projetoRepo.findById(projetoId);
    assertThat(atualizado).isPresent();
    assertThat(atualizado.get().getTarefas()).isEmpty();
  }

  @Test
  void deveRetornarVazioSeProjetoNaoExistir() {
    Optional<Projeto> resultado = projetoRepo.findById(new ProjetoId(UUID.randomUUID()));
    assertThat(resultado).isEmpty();
  }

  @Test
  void deveRecuperarTarefaEspecificaPorId() {
    var projetoId = new ProjetoId(UUID.randomUUID());
    var tarefaId = new TarefaId(UUID.randomUUID());

    var tarefa = new Tarefa(
        tarefaId,
        "Tarefa Alvo",
        "Descrição",
        DataVencimentoVO.now(),
        eStatusTarefaVO.PENDENTE,
        ePrioridadeVO.MEDIA,
        Set.of(),
        Set.of()
    );

    var projeto = new Projeto(projetoId, "Projeto", "Desc", 0L, Set.of(tarefa));
    projetoRepo.save(projeto);

    var resultado = projetoRepo.findTarefaByProjetoId(projetoId, tarefaId);
    assertThat(resultado).isPresent();
    assertThat(resultado.get().getTitulo()).isEqualTo("Tarefa Alvo");
  }

}
