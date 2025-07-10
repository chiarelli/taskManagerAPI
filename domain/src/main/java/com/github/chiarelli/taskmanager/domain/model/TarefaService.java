package com.github.chiarelli.taskmanager.domain.model;

import java.time.LocalDateTime;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.repository.iComentariosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iHistoricosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TarefaService {

  private final iTarefasRepository tarefaRepository;
  private final iComentariosRepository comentarioRepository;
  private final iHistoricosRepository historicoRepository;

  public void alterarStatusComHistorico(Tarefa tarefa, eStatusTarefaVO novoStatus, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      LocalDateTime.now(),
      "Alteração de Status",
      "Alterado de " + tarefa.getStatus() + " para " + novoStatus,
      autor
    );
    
    tarefa.alterarStatus(novoStatus, historico.getId());

    tarefaRepository.save(tarefa);
    historicoRepository.save(historico);
  }

  public void alterarDescricaoComHistorico(Tarefa tarefa, String novaDescricao, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      LocalDateTime.now(),
      "Alteração de Descrição",
      "Alterado de " + tarefa.getDescricao() + " para " + novaDescricao,
      autor
    );

    tarefa.alterarDescricao(novaDescricao, historico.getId());

    tarefaRepository.save(tarefa);
    historicoRepository.save(historico);
  }

  public void adicionarComentarioComHistorico(Tarefa tarefa, Comentario comentario) {
    var historico = new Historico(
      new HistoricoId(),
      LocalDateTime.now(),
      "Adição de Comentario",
      "Adicionado o comentario: " + comentario.getDescricao(),
      comentario.getAutor()
    );

    tarefa.adicionarComentario(comentario.getId(), historico.getId());

    comentarioRepository.save(comentario);
    historicoRepository.save(historico);
  }

  public void excluirTarefaComHistorico(Tarefa tarefa, AutorId autor) {
    var historico = new Historico(
      new HistoricoId(),
      LocalDateTime.now(),
      "Exclusão de Tarefa",
      "Excluido a tarefa: " + tarefa.getDescricao(),
      autor
    );

    tarefa.excluirTarefa();

    tarefaRepository.delete(tarefa);
    historicoRepository.save(historico);
  }

}
