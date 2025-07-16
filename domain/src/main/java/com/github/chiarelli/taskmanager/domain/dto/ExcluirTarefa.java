package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;

public record ExcluirTarefa(
  ProjetoId projetoId,
  TarefaId tarefaId
) { }
