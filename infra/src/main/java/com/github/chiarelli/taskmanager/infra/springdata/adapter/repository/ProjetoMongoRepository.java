package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ProjetoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.TarefaDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ProjetoSpringDataMongoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjetoMongoRepository implements iProjetoRepository {

  private final ProjetoSpringDataMongoRepository mongoRepository;

  @Override
  public Optional<Projeto> findById(ProjetoId id) {
    return mongoRepository.findById(id.getId())
        .map(ProjetoMongoRepository::toDomain);
  }

  @Override
  public void save(Projeto projeto) {
    mongoRepository.save(toDocument(projeto));
  }

  @Override
  public void delete(Projeto projeto) {
    mongoRepository.deleteById(projeto.getId().getId());
  }

  /* 
  @Override
  public List<Projeto> findAllByUserId(String userId) {
    return mongoRepository.findAllByUserId(userId)
        .stream()
        .map(this::toDomain)
        .toList();
  }
  */

  @Override
  public Optional<Tarefa> findTarefaByProjetoId(ProjetoId projetoId, TarefaId tarefaId) {
    return findById(projetoId)
        .flatMap(p -> p.getTarefas().stream()
          .filter(t -> t.getId().equals(tarefaId))
          .findFirst()
        );
  }

  @Override
  public void deleteTarefa(ProjetoId projetoId, TarefaId tarefaId) {
    findById(projetoId).ifPresent(projeto -> {
      projeto.removerTarefa(tarefaId);
      save(projeto); // Regrava o projeto atualizado
    });
  }

  @Override
  public List<Tarefa> findAllTarefasByProjetoId(ProjetoId idProjeto) {
    Optional<Projeto> optional = findById(idProjeto);

    if (optional.isEmpty()) {
      return List.of();
    }

    return optional.get().getTarefas().stream().toList();
  }
  
  // 🔄 Conversores
  public static ProjetoDocument toDocument(Projeto projeto) {
    ProjetoDocument doc = new ProjetoDocument();
        doc.setId(projeto.getId().getId());
        doc.setTitulo(projeto.getTitulo());
        doc.setDescricao(projeto.getDescricao());

        var mongoVersion = projeto.getVersion() - 1;
        doc.setVersion(projeto.getVersion() == 0L ? null : mongoVersion);

        doc.setTarefas(projeto.getTarefas().stream()
            .map(ProjetoMongoRepository::toDocument)
            .collect(Collectors.toSet()) );
    return doc;
  }

  public static TarefaDocument toDocument(Tarefa t) {
    TarefaDocument td = new TarefaDocument();
        td.setId(t.getId().getId());
        td.setTitulo(t.getTitulo());
        td.setDescricao(t.getDescricao());
        td.setStatus(t.getStatus());
        td.setPrioridade(t.getPrioridade());
        td.setDataVencimento(t.getDataVencimento().getDataVencimento());
    return td;
  }

  public static Tarefa toDomain(TarefaDocument td) {
   var comentarioIds = td.getComentarioIds().stream()
        .map(ComentarioId::new)
        .collect(Collectors.toSet());

    var historicoIds = td.getHistoricoIds().stream()
        .map(HistoricoId::new)
        .collect(Collectors.toSet());
    
    return new Tarefa(
        new TarefaId(td.getId()),
        td.getTitulo(),
        td.getDescricao(),
        new DataVencimentoVO(td.getDataVencimento()),
        td.getStatus(),
        td.getPrioridade(),
        comentarioIds,
        historicoIds
    );
  }

  public static Projeto toDomain(ProjetoDocument doc) {
    Projeto projeto;

    if (doc.getTarefas() != null) {

      projeto = new Projeto(
          new ProjetoId(doc.getId()),
          doc.getTitulo(),
          doc.getDescricao(),
          doc.getVersion(),
          doc.getTarefas().stream()
              .map(ProjetoMongoRepository::toDomain)
              .collect(Collectors.toSet()));

    } else {

      projeto = new Projeto(
          new ProjetoId(doc.getId()),
          doc.getTitulo(),
          doc.getDescricao(),
          doc.getVersion(),
          new HashSet<>());
    }

    return projeto;
  }

}
