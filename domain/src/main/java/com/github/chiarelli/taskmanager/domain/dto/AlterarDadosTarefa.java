package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;

public record AlterarDadosTarefa(
  ProjetoId projetoId,
  TarefaId tarefaId,
  String titulo,
  String descricao,
  DataVencimentoVO dataVencimento,
  Long projetoVersao
) {}
