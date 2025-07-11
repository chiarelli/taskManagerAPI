package com.github.chiarelli.taskmanager.domain.model;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

public interface iTarefaService {

  void alterarStatusComHistorico(Tarefa tarefa, eStatusTarefaVO novoStatus, AutorId autor);

  void alterarDescricaoComHistorico(Tarefa tarefa, String novaDescricao, AutorId autor);

  void adicionarComentarioComHistorico(Tarefa tarefa, Comentario comentario);

  void excluirTarefaComHistorico(Tarefa tarefa, AutorId autor);

}