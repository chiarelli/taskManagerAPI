package com.github.chiarelli.taskmanager.domain.model;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.repository.iComentariosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iHistoricosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TarefaService implements iTarefaService {

  private final iTarefasRepository tarefaRepository;
  private final iComentariosRepository comentarioRepository;
  private final iHistoricosRepository historicoRepository;

  @Override
  public void alterarStatusComHistorico(Tarefa tarefa, eStatusTarefaVO novoStatus, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Status",
      "Alterado de " + tarefa.getStatus() + " para " + novoStatus,
      autor
    );
    
    tarefa.alterarStatus(novoStatus, historico);

    tarefaRepository.save(tarefa);
    historicoRepository.save(historico);
  }

  @Override
  public void alterarDescricaoComHistorico(Tarefa tarefa, String novaDescricao, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Descrição",
      "Alterado de " + tarefa.getDescricao() + " para " + novaDescricao,
      autor
    );

    tarefa.alterarDescricao(novaDescricao, historico);

    tarefaRepository.save(tarefa);
    historicoRepository.save(historico);
  }

  @Override
  public void adicionarComentarioComHistorico(Tarefa tarefa, Comentario comentario) {
    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Adição de Comentario",
      "Adicionado o comentario: " + comentario.getDescricao(),
      comentario.getAutor()
    );

    tarefa.adicionarComentario(comentario.getId(), historico);

    comentarioRepository.save(comentario);
    historicoRepository.save(historico);
  }

  @Override
  public void excluirTarefaComHistorico(Tarefa tarefa, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Exclusão de Tarefa",
      "Excluido a tarefa: " + tarefa.getDescricao(),
      autor
    );

    tarefa.excluirTarefa();

    tarefaRepository.delete(tarefa);
    historicoRepository.save(historico);
  }

}
