package com.github.chiarelli.taskmanager.presentation.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.application.usecases.commands.AlterarDadosProjetoCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.CriarProjetoCommand;
import com.github.chiarelli.taskmanager.application.usecases.commands.ExcluirProjetoCommand;
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarProjetoPorIdQuery;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemPaginadaGenericQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.presentation.dtos.CreateProjectRequest;
import com.github.chiarelli.taskmanager.presentation.dtos.PageCollectionJsonResponse;
import com.github.chiarelli.taskmanager.presentation.dtos.ProjectRequest;
import com.github.chiarelli.taskmanager.presentation.dtos.ProjectResponse;

import io.github.jkratz55.mediator.core.Mediator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
@RequiredArgsConstructor
public class ProjectController {

  private final Mediator mediator;

  @PostMapping
  public ResponseEntity<ProjectResponse> create(@RequestBody @Valid CreateProjectRequest request) {
    ProjetoDTO projeto = mediator.dispatch(new CriarProjetoCommand(request.titulo(), request.descricao()));
    
    return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(projeto));
  }

  @GetMapping("{projectId}")
  public ResponseEntity<ProjectResponse> getById(@PathVariable(value = "projectId") UUID projectId) {
    ProjetoDTO projeto = mediator.dispatch(new BuscarProjetoPorIdQuery(new ProjetoId(projectId)));
    
    return ResponseEntity.ok(ProjectResponse.from(projeto));
  }

  @GetMapping
  public ResponseEntity<PageCollectionJsonResponse<ProjectResponse>> getAll(
    @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) Integer page,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") @Min(1) @Max(100) Integer pageSize
  ) {
    Page<ProjetoDTO> result = mediator.dispatch(new ListagemPaginadaGenericQuery<>(page, pageSize));

    return ResponseEntity.ok(new PageCollectionJsonResponse<>(result.map(ProjectResponse::from)));
  }
  
  @PutMapping("{projectId}")  
  public ResponseEntity<ProjectResponse> update(
    @PathVariable(value = "projectId") UUID projectId,
    @RequestBody @Valid ProjectRequest request
  ) {
    Command<ProjetoDTO> cmd = new AlterarDadosProjetoCommand(new ProjetoId(projectId), 
        request.titulo(), request.descricao(), request.version());
    ProjetoDTO projeto = mediator.dispatch(cmd);

    return ResponseEntity.ok(ProjectResponse.from(projeto));
  }

  @DeleteMapping("{projectId}")
  public ResponseEntity<Void> delete(@PathVariable(value = "projectId") UUID projectId) {
    mediator.dispatch(new ExcluirProjetoCommand(new ProjetoId(projectId)));
    return ResponseEntity.noContent().build();
  }  

}
