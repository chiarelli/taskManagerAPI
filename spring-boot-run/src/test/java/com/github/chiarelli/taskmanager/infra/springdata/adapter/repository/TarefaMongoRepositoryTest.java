package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ComentarioDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.HistoricoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ComentarioMongoRepository;

@Import(TarefaMongoRepository.class)
public class TarefaMongoRepositoryTest extends MongoTestContainer {

  @Autowired TarefaMongoRepository tarefaRepo;

  @Autowired ComentarioMongoRepository comentarioMongoRepo;

  @AfterEach
  void limparBanco() {
    mongoTemplate.getDb().drop();
  }

  @Test
  void devePersistirComentario() {
    // Arrange
    AutorId autorId = new AutorId(UUID.randomUUID().toString());
    TarefaId tarefaId = new TarefaId(UUID.randomUUID());
    ComentarioId comentarioId = new ComentarioId(UUID.randomUUID());
    
    Comentario comentario = new Comentario(
        comentarioId,
        new Date(),
        "Título de teste",
        "Primeiro comentário de teste",
        autorId,
        tarefaId);

    // Act
    tarefaRepo.saveComentario(tarefaId, comentario);

    // Assert
    ComentarioDocument doc = comentarioMongoRepo.findById(comentario.getId().getId()).orElseThrow();

    assertThat(doc.getId()).isEqualTo(comentario.getId().getId());
    assertThat(doc.getDataOcorrencia()).isEqualTo(comentario.getDataCriacao());
    assertThat(doc.getTitulo()).isEqualTo(comentario.getTitulo());
    assertThat(doc.getDescricao()).isEqualTo(comentario.getDescricao());
    assertThat(doc.getAutorId()).isEqualTo(UUID.fromString(autorId.getId()));
    assertThat(doc.getTarefaId()).isEqualTo(tarefaId.getId());
  }

  @Test
  void findComentarioByComentarioIdAndTarefaId_deveRecuperarComentarioSalvo() {
    // Arrange
    UUID comentarioUuid = UUID.randomUUID();
    UUID tarefaUuid = UUID.randomUUID();
    UUID autorUuid = UUID.randomUUID();

    ComentarioDocument doc = new ComentarioDocument();
    doc.setId(comentarioUuid);
    doc.setTitulo("Título salvo diretamente");
    doc.setDescricao("Descrição de comentário salvo");
    doc.setDataOcorrencia(new Date());
    doc.setAutorId(autorUuid);
    doc.setTarefaId(tarefaUuid);

    comentarioMongoRepo.save(doc);

    // Act
    List<Comentario> comentarios = tarefaRepo.findAllComentariosByTarefaId(new TarefaId(tarefaUuid));
    Optional<Comentario> resultado = comentarios.stream().findFirst();

    // Assert
    assertThat(resultado).isPresent();
    assertThat(resultado.get().getId().getId()).isEqualTo(comentarioUuid);
    assertThat(resultado.get().getTitulo()).isEqualTo(doc.getTitulo());
    assertThat(resultado.get().getDescricao()).isEqualTo(doc.getDescricao());
    assertThat(resultado.get().getDataCriacao()).isEqualTo(doc.getDataOcorrencia());
    assertThat(resultado.get().getAutor().getId()).isEqualTo(autorUuid.toString());
    assertThat(resultado.get().getTarefaId().getId()).isEqualTo(tarefaUuid);
    assertThat(resultado.get().getVersion()).isEqualTo(0L);
  }

  @Test
  void saveHistorico_devePersistirHistoricoNoMongo() {
    // Arrange
    UUID historicoUuid = UUID.randomUUID();
    UUID tarefaUuid = UUID.randomUUID();
    UUID autorUuid = UUID.randomUUID();

    TarefaId tarefaId = new TarefaId(tarefaUuid);
    AutorId autorId = new AutorId(autorUuid.toString());
    Historico historico = new Historico(
        new HistoricoId(historicoUuid),
        new Date(),
        "Mensagem de teste",
        "Descrição de teste",
        autorId);

    // Act
    tarefaRepo.saveHistorico(tarefaId, historico);

    Optional<Historico> resultado = tarefaRepo.findAllHistoricosByTarefaId(tarefaId).stream().findFirst();

    // Assert
    assertThat(resultado).isPresent();
    assertThat(resultado.get().getId().getId()).isEqualTo(historicoUuid);
    assertThat(resultado.get().getTitulo()).isEqualTo(historico.getTitulo());
    assertThat(resultado.get().getDescricao()).isEqualTo(historico.getDescricao());
    assertThat(resultado.get().getDataOcorrencia()).isEqualTo(historico.getDataOcorrencia());
    assertThat(resultado.get().getAutor().getId()).isEqualTo(autorUuid.toString());
  }

  @Test
  void findAllComentariosByTarefaId_deveRetornarTodosOsComentariosDaTarefa() {
    // Arrange
    UUID tarefaUuid = UUID.randomUUID();
    UUID autor1 = UUID.randomUUID();
    UUID autor2 = UUID.randomUUID();
    UUID comentario1 = UUID.randomUUID();
    UUID comentario2 = UUID.randomUUID();

    ComentarioDocument doc1 = new ComentarioDocument();
    doc1.setId(comentario1);
    doc1.setTitulo("Comentário 1");
    doc1.setDescricao("Primeiro comentário");
    doc1.setDataOcorrencia(new Date());
    doc1.setAutorId(autor1);
    doc1.setTarefaId(tarefaUuid);

    ComentarioDocument doc2 = new ComentarioDocument();
    doc2.setId(comentario2);
    doc2.setTitulo("Comentário 2");
    doc2.setDescricao("Segundo comentário");
    doc2.setDataOcorrencia(new Date());
    doc2.setAutorId(autor2);
    doc2.setTarefaId(tarefaUuid);

    comentarioMongoRepo.saveAll(List.of(doc1, doc2));

    // Act
    List<Comentario> resultado = tarefaRepo.findAllComentariosByTarefaId(new TarefaId(tarefaUuid));

    // Assert
    assertThat(resultado).hasSize(2);
    assertThat(resultado)
        .extracting(Comentario::getId)
        .extracting(ComentarioId::getId)
        .containsExactlyInAnyOrder(comentario1, comentario2);

    assertThat(resultado)
        .extracting(Comentario::getTitulo)
        .containsExactlyInAnyOrder("Comentário 1", "Comentário 2");

    assertThat(resultado)
        .extracting(Comentario::getAutor)
        .extracting(AutorId::getId)
        .extracting(UUID::fromString)
        .containsExactlyInAnyOrder(autor1, autor2);
  }

  @Test
  void findAllHistoricosByTarefaId_deveRetornarTodosOsHistoricosDaTarefa() {
    // Arrange
    UUID tarefaUuid = UUID.randomUUID();
    TarefaId tarefaId = new TarefaId(tarefaUuid);
    UUID autor1 = UUID.randomUUID();
    UUID autor2 = UUID.randomUUID();
    UUID historico1 = UUID.randomUUID();
    UUID historico2 = UUID.randomUUID();

    HistoricoDocument doc1 = new HistoricoDocument();
    doc1.setId(historico1);
    doc1.setTitulo("Histórico 1");
    doc1.setDescricao("Primeiro histórico");
    doc1.setDataOcorrencia(new Date());
    doc1.setAutorId(autor1);
    doc1.setTarefaId(tarefaUuid);

    HistoricoDocument doc2 = new HistoricoDocument();
    doc2.setId(historico2);
    doc2.setTitulo("Histórico 2");
    doc2.setDescricao("Segundo histórico");
    doc2.setDataOcorrencia(new Date());
    doc2.setAutorId(autor2);
    doc2.setTarefaId(tarefaUuid);

    tarefaRepo.saveHistorico(tarefaId, HistoricoMapper.toDomain(doc1));
    tarefaRepo.saveHistorico(tarefaId, HistoricoMapper.toDomain(doc2));

    // Act
    List<Historico> resultado = tarefaRepo.findAllHistoricosByTarefaId(tarefaId);

    // Assert
    assertThat(resultado).hasSize(2);
    assertThat(resultado)
        .extracting(Historico::getId)
        .extracting(HistoricoId::getId)
        .containsExactlyInAnyOrder(historico1, historico2);

    assertThat(resultado)
        .extracting(Historico::getTitulo)
        .containsExactlyInAnyOrder("Histórico 1", "Histórico 2");

    assertThat(resultado)
        .extracting(Historico::getAutor)
        .extracting(AutorId::getId)
        .extracting(UUID::fromString)
        .containsExactlyInAnyOrder(autor1, autor2);
  }

  @Test
  void findByComentarioIdETarefaId_comIdsInexistentes_deveRetornarVazio() {
    // Arrange
    UUID tarefaUuid = UUID.randomUUID();
    UUID comentarioUuid = UUID.randomUUID();
    TarefaId tarefaId = new TarefaId(tarefaUuid);
    ComentarioId comentarioId = new ComentarioId(comentarioUuid);

    // Act
    List<Comentario> comentarios = tarefaRepo.findAllComentariosByTarefaId(tarefaId);
    Optional<Comentario> resultado = comentarios.stream().filter(c -> c.getId().equals(comentarioId)).findFirst();

    // Assert
    assertThat(resultado).isEmpty();
  }

}
