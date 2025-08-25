package com.github.chiarelli.taskmanager.presentation.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarComentarioCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarEAdicionarComentarioCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirComentarioCommand;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemComentariosDeTarefaQuery;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.presentation.dtos.CommentRequest;
import com.github.chiarelli.taskmanager.presentation.dtos.CommentResponse;
import com.github.chiarelli.taskmanager.presentation.dtos.PageCollectionJsonResponse;

import io.github.jkratz55.mediator.core.Mediator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class CommentsController {

  private final Mediator mediator;

  @PostMapping("{projectId}/tasks/{taskId}/comments")
  public ResponseEntity<CommentResponse> addComment(
    @PathVariable(value = "projectId") UUID projectId,
    @PathVariable(value = "taskId") UUID taskId, 
    @RequestBody @Valid CommentRequest request
  ) {

    var comentario = mediator.dispatch(new CriarEAdicionarComentarioCommand(
      new ProjetoId(projectId),
      new TarefaId(taskId),
      request.titulo(),
      request.descricao(),
      new AutorId(UUID.randomUUID().toString()) // TODO: pegar o autor logado
    ));

    return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comentario));
  }

  @GetMapping("{projectId}/tasks/{taskId}/comments")
  public ResponseEntity<PageCollectionJsonResponse<CommentResponse>> getComments(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId,
    @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) Integer page,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") @Min(1) @Max(100) Integer pageSize
  ) {
    Page<ComentarioDTO> result = mediator.dispatch(new ListagemComentariosDeTarefaQuery(
      new ProjetoId(projectId), new TarefaId(taskId), page, pageSize));

    return ResponseEntity.ok().body(new PageCollectionJsonResponse<>(result.map(CommentResponse::from)));
  }

  @PutMapping("{projectId}/tasks/{taskId}/comments/{commentId}")
  public ResponseEntity<CommentResponse> update(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId,
    @PathVariable(value = "commentId") UUID commentId,
    @RequestBody @Valid CommentRequest request
  ) {
    ComentarioDTO dto = mediator.dispatch(new AlterarComentarioCommand(
      new ProjetoId(projectId),
      new TarefaId(taskId),
      new ComentarioId(commentId),
      request.titulo(),
      request.descricao(),
      new AutorId(UUID.randomUUID().toString()) // TODO: pegar o autor logado
    ));
    
    return ResponseEntity.ok().body(CommentResponse.from(dto));
  }

  @DeleteMapping("{projectId}/tasks/{taskId}/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(
    @PathVariable(value = "projectId") UUID projectId, 
    @PathVariable(value = "taskId") UUID taskId, 
    @PathVariable(value = "commentId") UUID commentId
  ) {
    mediator.dispatch(new ExcluirComentarioCommand(
      new ProjetoId(projectId), new TarefaId(taskId), new ComentarioId(commentId)));

    return ResponseEntity.noContent().build();
  }

}
