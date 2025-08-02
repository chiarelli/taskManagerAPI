package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public record AlterarStatusTarefa(
  ProjetoId projetoId,
  TarefaId tarefaId,
  eStatusTarefaVO status,
  Long projetoVersao
) {

}
