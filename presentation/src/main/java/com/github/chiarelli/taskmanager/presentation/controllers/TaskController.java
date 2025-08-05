package com.github.chiarelli.taskmanager.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.chiarelli.taskmanager.application.dtos.HistoricoDTO;
import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosTarefaCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarStatusTarefaCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarTarefaCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirTarefaCommand;
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarTarefaPorIdQuery;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemHistoricosDaTarefaQuery;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemTarefasDoProjetoQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.presentation.dtos.CreateTaskRequest;
import com.github.chiarelli.taskmanager.presentation.dtos.HistoricalResponse;
import com.github.chiarelli.taskmanager.presentation.dtos.PageCollectionJsonResponse;
import com.github.chiarelli.taskmanager.presentation.dtos.TaskResponse;
import com.github.chiarelli.taskmanager.presentation.dtos.UpdateStatusTaskRequest;
import com.github.chiarelli.taskmanager.presentation.dtos.UpdateTaskRequest;

import io.github.jkratz55.mediator.core.Mediator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
@RequiredArgsConstructor
public class TaskController {

  private final Mediator mediator;

  @PostMapping("{projectId}/tasks")
  public ResponseEntity<TaskResponse> addTask(
    @PathVariable(value = "projectId") UUID projectId, 
    @RequestBody @Valid CreateTaskRequest request
  ) {
    TarefaDTO tarefa = mediator.dispatch(new CriarTarefaCommand(
        new ProjetoId(projectId),
        request.titulo(), 
        request.descricao(),
        DataVencimentoVO.of(request.dataVencimento()), 
        request.prioridade(),
        request.status()
    ));
    
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(TaskResponse.from(tarefa));
  }

  @GetMapping("{projectId}/tasks/{taskId}")
  public ResponseEntity<TaskResponse> getTask(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId
  ) {
    TarefaDTO tarefa = mediator.dispatch(new BuscarTarefaPorIdQuery(new ProjetoId(projectId), new TarefaId(taskId)));

    return ResponseEntity.ok().body(TaskResponse.from(tarefa));
  }

  @GetMapping("{projectId}/tasks")
  public ResponseEntity<List<TaskResponse>> getTasks(
    @PathVariable(value = "projectId") UUID projectId
  ) {
    var query = new ListagemTarefasDoProjetoQuery(new ProjetoId(projectId));
    List<TarefaDTO> result = mediator.dispatch(query);

    return ResponseEntity.ok().body(result.stream().map(TaskResponse::from).toList());
  }

  @PutMapping("{projectId}/tasks/{taskId}")
  public ResponseEntity<TaskResponse> updateTask(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId, 
    @RequestBody @Valid UpdateTaskRequest request
  ) {
    TarefaDTO tarefa = mediator.dispatch(new AlterarDadosTarefaCommand(
      new ProjetoId(projectId), 
      new TarefaId(taskId), 
      request.titulo(), 
      request.descricao(),
      DataVencimentoVO.of(request.dataVencimento()),
      request.version()
    ));

    return ResponseEntity.ok().body(TaskResponse.from(tarefa));
  }

  @PatchMapping("{projectId}/tasks/{taskId}/status")
  public ResponseEntity<TaskResponse> updateStatusTask(
      @PathVariable(value = "projectId") UUID projectId,
      @PathVariable(value = "taskId") UUID taskId,
      @RequestBody @Valid UpdateStatusTaskRequest request
  ) {
    TarefaDTO tarefa = mediator.dispatch(new AlterarStatusTarefaCommand(
        new ProjetoId(projectId),
        new TarefaId(taskId),
        request.status(),
        request.version()));

    return ResponseEntity.ok().body(TaskResponse.from(tarefa));
  }

  @DeleteMapping("{projectId}/tasks/{taskId}")
  public ResponseEntity<Void> deleteTask(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId
  ) {
    mediator.dispatch(new ExcluirTarefaCommand(new ProjetoId(projectId), new TarefaId(taskId)));
    
    return ResponseEntity.noContent().build();
  }

  @GetMapping("{projectId}/tasks/{taskId}/historical")
  public ResponseEntity<PageCollectionJsonResponse<HistoricalResponse>> getHistoricalTasks(
    @PathVariable(value = "projectId") UUID projectId,
    @PathVariable(value = "taskId") UUID taskId,
    @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) Integer page,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") @Min(1) @Max(100) Integer pageSize
  ) {
    Page<HistoricoDTO> result = mediator.dispatch(new ListagemHistoricosDaTarefaQuery(new ProjetoId(projectId), new TarefaId(taskId), page, pageSize));
    Page<HistoricalResponse> pageResponse = result.map(HistoricalResponse::form);

    return ResponseEntity.ok().body(new PageCollectionJsonResponse<>(pageResponse));
  }

}
