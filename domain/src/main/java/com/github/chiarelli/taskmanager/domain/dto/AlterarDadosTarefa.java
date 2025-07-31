package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;

public record AlterarDadosTarefa(
  ProjetoId projetoId,
  TarefaId tarefaId,
  String titulo,
  String descricao,
  DataVencimentoVO dataVencimento,
  ePrioridadeVO prioridade,
  Long projetoVersao
) {}
