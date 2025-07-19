package com.github.chiarelli.taskmanager.domain.model;

import java.util.ArrayList;
import java.util.Date;

import com.github.chiarelli.taskmanager.domain.dto.AlterarTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.HistoricoAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.ITarefaService;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TarefaService implements ITarefaService {

  private final iTarefasRepository tarefaRepository;
  private final iProjetoRepository projetoRepository;
  private final iDomainEventBuffer eventBuffer;
  
  @Override
  public ServiceResult<Void> alterarStatusComHistorico(AlterarTarefa data, AutorId autor) {

    var resp = loadTarefaByProjetoIdAndTarefaId(data.projetoId(), data.tarefaId());

    Tarefa tarefa = resp.tarefa();
    Projeto projeto = resp.projeto();    

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Status",
      "Alterado de " + tarefa.getStatus() + " para " + data.status(),
      autor
    );
    
    projeto.alterarStatusTarefa(tarefa.getId(), data.status(), historico);

    projetoRepository.save(projeto);
    tarefaRepository.saveHistorico(tarefa.getId(), historico);
    
    eventBuffer.collectFrom(projeto);
    eventBuffer.collectFrom(tarefa);

    return new ServiceResult<>(null, eventBuffer.flushEvents());
  }
  
  @Override
  public ServiceResult<Void> alterarDescricaoComHistorico(AlterarTarefa data, AutorId autor) {
    var resp = loadTarefaByProjetoIdAndTarefaId(data.projetoId(), data.tarefaId());

    Tarefa tarefa = resp.tarefa();
    Projeto projeto = resp.projeto();

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Descrição",
      "Alterado de " + tarefa.getDescricao() + " para " + data.descricao(),
      autor
    );

    projeto.alterarDescricaoTarefa(tarefa.getId(), data.descricao(), historico);

    projetoRepository.save(projeto);
    tarefaRepository.saveHistorico(tarefa.getId(), historico);

    eventBuffer.collectFrom(projeto);
    eventBuffer.collectFrom(tarefa);

    return new ServiceResult<>(null, eventBuffer.flushEvents());
  }
  
  @Override
  public ServiceResult<Void> adicionarComentarioComHistorico(Tarefa tarefa, Comentario comentario) {
    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Adição de Comentario",
      "Adicionado o comentario: " + comentario.getDescricao(),
      comentario.getAutor()
    );

    tarefa.adicionarComentario(comentario, historico);

    tarefaRepository.saveComentario(tarefa.getId(), comentario);
    tarefaRepository.saveHistorico(tarefa.getId(), historico);

    eventBuffer.collectFrom(tarefa);

    var payload = new HistoricoAdicionadoEvent.Payload(historico.getId(),
        historico.getDataOcorrencia(), historico.getTitulo(), 
        historico.getDescricao(), historico.getAutor());
    
    var events = new ArrayList<>(eventBuffer.flushEvents());

    events.add(new HistoricoAdicionadoEvent(tarefa, payload)); // Adiciona o evento de histórico

    return new ServiceResult<>(null, events);
  }
  
  @Override
  public ServiceResult<Void> excluirTarefaComHistorico(ExcluirTarefa data, AutorId autor) {
    var resp = loadTarefaByProjetoIdAndTarefaId(data.projetoId(), data.tarefaId());

    Tarefa tarefa = resp.tarefa();
    Projeto projeto = resp.projeto();

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Exclusão de Tarefa",
      "Excluido a tarefa: " + tarefa.getDescricao(),
      autor
    );

    projeto.removerTarefa(data.tarefaId());

    projetoRepository.deleteTarefa(projeto.getId(), tarefa.getId());
    tarefaRepository.saveHistorico(tarefa.getId(), historico);

    eventBuffer.collectFrom(projeto);
    eventBuffer.collectFrom(tarefa);

    var payload = new HistoricoAdicionadoEvent.Payload(historico.getId(),
        historico.getDataOcorrencia(), historico.getTitulo(), 
        historico.getDescricao(), historico.getAutor());
    
    var events = new ArrayList<>(eventBuffer.flushEvents());

    events.add(new HistoricoAdicionadoEvent(tarefa, payload)); // Adiciona o evento de histórico

    return new ServiceResult<>(null, events);
  }

  private ProjectWithYourTask loadTarefaByProjetoIdAndTarefaId(ProjetoId projetoId, TarefaId tarefaId) {
    Projeto projeto = projetoRepository.findById(projetoId)
        .orElseThrow(() -> new DomainException("Projeto %s não existe".formatted(projetoId)));

    Tarefa tarefa = projeto.getTarefas().stream()
      .filter(t -> t.getId().equals(tarefaId))  
      .findFirst()
      .orElseThrow(() -> new DomainException("Tarefa %s não pertence ao projeto %s".formatted(tarefaId, projetoId)));
    
    return new ProjectWithYourTask(projeto, tarefa);
  }

}

record ProjectWithYourTask(
  Projeto projeto,
  Tarefa tarefa
){}