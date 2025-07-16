package com.github.chiarelli.taskmanager.domain.shared;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public interface ITarefaService {

  ServiceResult<Void> alterarStatusComHistorico(AlterarTarefa data, AutorId autor);

  ServiceResult<Void> alterarDescricaoComHistorico(AlterarTarefa data, AutorId autor);

  ServiceResult<Void> adicionarComentarioComHistorico(Tarefa tarefa, Comentario comentario);

  ServiceResult<Void> excluirTarefaComHistorico(ExcluirTarefa data, AutorId autor);

}