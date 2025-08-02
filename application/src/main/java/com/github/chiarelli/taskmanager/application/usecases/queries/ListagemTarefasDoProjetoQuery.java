package com.github.chiarelli.taskmanager.application.usecases.queries;

import java.util.List;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Query;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotNull;

public record ListagemTarefasDoProjetoQuery(
  
  @NotNull
  ProjetoId projetoId

) implements Query<List<TarefaDTO>> {

  public static TarefaDTO toTarefaDTO(Tarefa tarefa) {
    return TarefaDTO.fromIgnoringCollections(tarefa);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }
  
}
