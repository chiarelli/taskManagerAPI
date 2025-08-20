package com.github.chiarelli.taskmanager.application.usecases.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.shared.Query;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListagemComentariosDeTarefaQuery(

  @NotNull(message = "O id do projeto é obrigatorio")
  ProjetoId projetoId,
  
  @NotNull(message = "O id da tarefa é obrigatorio")
  TarefaId tarefaId,

  @Min(1)
  Integer page,

  @Min(1)
  Integer pageSize
  
) implements Query<Page<ComentarioDTO>> {

  public Pageable toPageable() {
    return PageRequest.of(page - 1, pageSize);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
