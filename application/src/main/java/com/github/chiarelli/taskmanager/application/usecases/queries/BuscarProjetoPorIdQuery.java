package com.github.chiarelli.taskmanager.application.usecases.queries;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Query;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.NotNull;

public record BuscarProjetoPorIdQuery(
  
  @NotNull(message = "O id do projeto nao pode ser nulo") 
  ProjetoId projetoId

) implements Query<ProjetoDTO> {

  public static ProjetoDTO toProjetoDTO(Projeto projeto) {
    var tarefas = projeto.getTarefas().stream()
      .map(TarefaDTO::fromIgnoringCollections)
      .toList();

    var target = new ProjetoDTO();
        target.setId(projeto.getId());
        target.setTitulo(projeto.getTitulo());
        target.setDescricao(projeto.getDescricao());
        target.setVersion(projeto.getVersion());
        target.setTarefas(tarefas);
        
    return target;
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
