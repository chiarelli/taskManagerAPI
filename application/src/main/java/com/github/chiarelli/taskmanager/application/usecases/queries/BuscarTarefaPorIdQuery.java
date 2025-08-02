package com.github.chiarelli.taskmanager.application.usecases.queries;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Query;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotNull;

public record BuscarTarefaPorIdQuery(
  
  @NotNull
  ProjetoId projetoId,

  @NotNull
  TarefaId tarefaId

) implements Query<TarefaDTO> {

  public static TarefaDTO toTarefaDTO(Tarefa tarefa) {
    return TarefaDTO.fromIgnoringCollections(tarefa);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
