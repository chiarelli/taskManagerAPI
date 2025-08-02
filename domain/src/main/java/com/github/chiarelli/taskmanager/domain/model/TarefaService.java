package com.github.chiarelli.taskmanager.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.chiarelli.taskmanager.domain.dto.AlterarComentario;
import com.github.chiarelli.taskmanager.domain.dto.AlterarDadosTarefa;
import com.github.chiarelli.taskmanager.domain.dto.AlterarStatusTarefa;
import com.github.chiarelli.taskmanager.domain.dto.CriarComentario;
import com.github.chiarelli.taskmanager.domain.dto.ExcluirTarefa;
import com.github.chiarelli.taskmanager.domain.dto.ServiceResult;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.HistoricoId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.event.HistoricoAdicionadoEvent;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TarefaService implements iTarefaService {

  private final iTarefasRepository tarefaRepository;
  private final iProjetoRepository projetoRepository;
  private final iDomainEventBuffer eventBuffer;
  
  @Override
  public ServiceResult<Tarefa> alterarStatusComHistorico(AlterarStatusTarefa data, AutorId autor) {

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

    return new ServiceResult<>(tarefa, eventBuffer.flushEvents());
  }
  
  @Override
  public ServiceResult<Tarefa> alterarDadosComHistorico(AlterarDadosTarefa data, AutorId autor) {
    var resp = loadTarefaByProjetoIdAndTarefaId(data.projetoId(), data.tarefaId());

    Tarefa tarefa = resp.tarefa();
    Projeto projeto = resp.projeto();

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de dados da Tarefa",
      data.toString(),
      autor
    );

    projeto.alterarDadosTarefa(tarefa.getId(), data.titulo(), data.descricao(), 
        data.dataVencimento(), data.prioridade(), data.projetoVersao(), historico);

    projetoRepository.save(projeto);
    tarefaRepository.saveHistorico(tarefa.getId(), historico);

    eventBuffer.collectFrom(projeto);
    eventBuffer.collectFrom(tarefa);

    return new ServiceResult<>(tarefa, eventBuffer.flushEvents());
  }
  
  @Override
  public ServiceResult<Comentario> adicionarComentarioComHistorico(ProjetoId projetoId, TarefaId tarefaId, CriarComentario data) {
    var resp = loadTarefaByProjetoIdAndTarefaId(projetoId, tarefaId);

    Tarefa tarefa = resp.tarefa();
    Projeto projeto = resp.projeto();

    Comentario comentario = Comentario.criarNovoComentario(data.titulo(), data.descricao(), data.autor(), tarefa.getId());

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Adição de Comentario",
      "Adicionado o comentario: " + comentario.getDescricao(),
      comentario.getAutor()
    );

    tarefa.adicionarComentario(projeto, comentario, historico);

    tarefaRepository.saveComentario(tarefa.getId(), comentario);
    tarefaRepository.saveHistorico(tarefa.getId(), historico);

    eventBuffer.collectFrom(tarefa);
    eventBuffer.collectFrom(comentario);

    return new ServiceResult<>(comentario, eventBuffer.flushEvents());
  }
  
  @Override
  public ServiceResult<Comentario> alterarComentarioComHistorico(ProjetoId projetoId, TarefaId tarefaId, AlterarComentario data) {
    var resp = loadTarefaByProjetoIdAndTarefaId(projetoId, tarefaId);

    Tarefa tarefa = resp.tarefa();

    Comentario comentario = tarefaRepository.findComentarioByComentarioIdAndTarefaId(tarefa.getId(), data.id())
        .orElseThrow(() -> new DomainException("Comentario %s nao pertence à tarefa %s"));

    var historico = new Historico(
      new HistoricoId(),
      new Date(),
      "Alteração de Comentario",
      "Alterado o comentario: " + comentario.getDescricao(),
      comentario.getAutor()
    );

    comentario.atualizarComentario(data);

    tarefaRepository.saveComentario(tarefa.getId(), comentario);
    tarefaRepository.saveHistorico(tarefaId, historico);

    eventBuffer.collectFrom(tarefa);
    eventBuffer.collectFrom(comentario);

    var payload = new HistoricoAdicionadoEvent.Payload(tarefa.getId(), historico.getId(),
    historico.getDataOcorrencia(), historico.getTitulo(), 
    historico.getDescricao(), historico.getAutor());
    
    var event = new HistoricoAdicionadoEvent(resp.projeto(), payload);

    List<AbstractDomainEvent<?>> events = new ArrayList<>(eventBuffer.flushEvents());
                                 events.add(event);

    return new ServiceResult<>(comentario, events);
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

    var payload = new HistoricoAdicionadoEvent.Payload(tarefa.getId(), historico.getId(),
        historico.getDataOcorrencia(), historico.getTitulo(), 
        historico.getDescricao(), historico.getAutor());
    
    var events = new ArrayList<>(eventBuffer.flushEvents());

    events.add(new HistoricoAdicionadoEvent(projeto, payload)); // Adiciona o evento de histórico

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