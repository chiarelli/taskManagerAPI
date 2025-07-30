package com.github.chiarelli.taskmanager.presentation.dtos;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProjectResponse {

  private final UUID id;
  private final String titulo;
  private final String descricao;
  private final Long version;
  private final List<TaskResponse> tarefas;

  public List<TaskResponse> getTarefas() {
    return Collections.unmodifiableList(tarefas);
  }

  public static ProjectResponse from(ProjetoDTO projeto) {
    return new ProjectResponse(
      projeto.getId().getId(),
      projeto.getTitulo(),
      projeto.getDescricao(),
      projeto.getVersion(),
      projeto.getTarefas().stream()
        .map(TaskResponse::from)
        .toList()
    );
  }

}
