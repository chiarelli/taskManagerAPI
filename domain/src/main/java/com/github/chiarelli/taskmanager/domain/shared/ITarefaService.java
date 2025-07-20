package com.github.chiarelli.taskmanager.domain.shared;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;

public interface ITarefaService {

  ServiceResult<Void> alterarStatusComHistorico(AlterarTarefa data, AutorId autor);

  ServiceResult<Void> alterarDescricaoComHistorico(AlterarTarefa data, AutorId autor);

  ServiceResult<Void> adicionarComentarioComHistorico(ProjetoId projetoId, TarefaId tarefaId, Comentario comentario);

  ServiceResult<Void> excluirTarefaComHistorico(ExcluirTarefa data, AutorId autor);

}