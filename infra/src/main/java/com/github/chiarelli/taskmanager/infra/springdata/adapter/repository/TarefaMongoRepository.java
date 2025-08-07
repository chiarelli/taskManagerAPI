package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Historico;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ComentarioDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.HistoricoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.ComentarioMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.HistoricoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ComentarioMongoRepository;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.HistoricoMongoRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TarefaMongoRepository implements iTarefasRepository {

  private final ComentarioMongoRepository comentarioRepo;
  private final HistoricoMongoRepository historicoRepo;

  @Override
  public List<Historico> findAllHistoricosByTarefaId(TarefaId id) {
    List<HistoricoDocument> docs = historicoRepo.findAllByTarefaId(id.getId());
    return docs.stream().map(HistoricoMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Comentario> findAllComentariosByTarefaId(TarefaId id) {
    List<ComentarioDocument> docs = comentarioRepo.findAllByTarefaId(id.getId());
    return docs.stream().map(ComentarioMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Optional<Comentario> findComentarioByComentarioIdAndTarefaId(TarefaId tarefaId, ComentarioId comentarioId) {
    return comentarioRepo.findByIdAndTarefaId(comentarioId.getId(), tarefaId.getId())
      .map(ComentarioMapper::toDomain);
  }

  @Override
  public void saveHistorico(TarefaId tarefaId, Historico historico) {
    HistoricoDocument doc = HistoricoMapper.toDocument(historico);
    doc.setTarefaId(tarefaId.getId());
    historicoRepo.save(doc);
  }

  @Override
  public void saveComentario(TarefaId tarefaId, Comentario comentario) {
    ComentarioDocument doc = ComentarioMapper.toDocument(comentario);
    doc.setTarefaId(tarefaId.getId());
    comentarioRepo.save(doc);
  }

  @Override
  public void deleteComentario(TarefaId tarefaId, ComentarioId comentarioId) {
    comentarioRepo.deleteByIdAndTarefaId(comentarioId.getId(), tarefaId.getId());
  }

}
