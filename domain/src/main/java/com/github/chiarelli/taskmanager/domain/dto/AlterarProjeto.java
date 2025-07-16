package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;

public record AlterarProjeto(
  ProjetoId projetoId,
  String titulo,
  String descricao
) {

}
