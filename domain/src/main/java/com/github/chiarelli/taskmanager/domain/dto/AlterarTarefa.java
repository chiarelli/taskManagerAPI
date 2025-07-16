package com.github.chiarelli.taskmanager.domain.dto;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public record AlterarTarefa(
  ProjetoId projetoId,
  TarefaId tarefaId,
  String titulo,
  String descricao,
  DataVencimentoVO dataVencimento,
  eStatusTarefaVO status,
  ePrioridadeVO prioridade
) {}
