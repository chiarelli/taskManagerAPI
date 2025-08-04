package com.github.chiarelli.taskmanager.application.usecases.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTO;
import com.github.chiarelli.taskmanager.application.shared.Query;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListagemHistoricosDaTarefaQuery(

  @NotNull
  ProjetoId projetoId,
  
  @NotNull
  TarefaId tarefaId,

  @Min(1)
  Integer page,

  @Min(1)
  Integer pageSize

) implements Query<Page<HistoricoDTO>> {

  public Pageable toPageable() {
    return PageRequest.of(page - 1, pageSize);
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
