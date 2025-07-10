package com.github.chiarelli.taskmanager.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ProjetoRemovidoEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaAdicionadaEvent;
import com.github.chiarelli.taskmanager.domain.event.TarefaRemovidaEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Projeto extends AbstractModelEvents implements iDefaultAggregate {

  private ProjetoId id;
  private String titulo;
  private String descricao;

  private Long version = 0L;

  private Set<Tarefa> tarefas = new HashSet<>();

  public Projeto(String titulo, String descricao) {
    this.titulo = titulo;
    this.descricao = descricao;
  }

  // Métodos de negócio

  public void adicionarTarefa(Tarefa novaTarefa) {
    if (this.tarefas.size() >= 20) {
      throw new DomainException("Limite de tarefas atingido");
    }
    this.tarefas.add(novaTarefa);

    var payload = new TarefaAdicionadaEvent.Payload(this.id, novaTarefa.getId());
    this.addEvent(new TarefaAdicionadaEvent(this, payload));
  }

  public void removerTarefa(TarefaId id) {
    if(tarefas.stream().noneMatch(t -> t.getId().equals(id))) {
      throw new DomainException("Tarefa %s não pertence ao projeto %s"
      .formatted(id.toString(), this.id.toString()));
    }
    tarefas.removeIf(t -> t.getId().equals(id));

    var payload = new TarefaRemovidaEvent.Payload(this.id, id);
    this.addEvent(new TarefaRemovidaEvent(this, payload));
  }

  public Optional<Tarefa> buscarTarefaPorId(TarefaId tarefaId) {
    return tarefas.stream()
        .filter(t -> t.getId().equals(tarefaId))
        .findFirst();
  }
  
  public void removerProjeto() {
    boolean existeTarefaPendente = tarefas.stream()
        .anyMatch(t -> t.getStatus() == eStatusTarefaVO.PENDENTE);

    if (existeTarefaPendente) {
      throw new DomainException("Não é possível remover o projeto: há tarefas pendentes.");
    }

    this.addEvent(new ProjetoRemovidoEvent(this, this.id));
  }

  // Metodos getters

  public Set<Tarefa> getTarefas() {
    return Collections.unmodifiableSet(tarefas);
  }

  @Override
  public Long getVersion() {
    return version;
  }

  @Override
  public String getIdAsString() {
    return id.toString();
  }
  
}
