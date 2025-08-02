package com.github.chiarelli.taskmanager.domain.shared;

import com.github.chiarelli.taskmanager.domain.dto.AlterarComentario;
import com.github.chiarelli.taskmanager.domain.dto.AlterarDadosTarefa;
import com.github.chiarelli.taskmanager.domain.dto.AlterarStatusTarefa;
import com.github.chiarelli.taskmanager.domain.dto.CriarComentario;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;

public interface iTarefaService {

  ServiceResult<Tarefa> alterarStatusComHistorico(AlterarStatusTarefa data, AutorId autor);

  ServiceResult<Tarefa> alterarDadosComHistorico(AlterarDadosTarefa data, AutorId autor);

  ServiceResult<Comentario> adicionarComentarioComHistorico(ProjetoId projetoId, TarefaId tarefaId, CriarComentario comentario);

  ServiceResult<Comentario> alterarComentarioComHistorico(ProjetoId projetoId, TarefaId tarefaId, AlterarComentario data);

  ServiceResult<Void> excluirTarefaComHistorico(ExcluirTarefa data, AutorId autor);

}