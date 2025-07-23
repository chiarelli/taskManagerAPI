package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;

public record CriarComentario(
    TarefaId tarefaId,
    String titulo,
    String descricao,
    AutorId autor
) {

}
