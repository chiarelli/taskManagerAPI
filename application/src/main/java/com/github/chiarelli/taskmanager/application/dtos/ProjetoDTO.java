package com.github.chiarelli.taskmanager.application.dtos;

import java.util.ArrayList;
import java.util.List;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.model.Projeto;

import lombok.Data;

@Data
public class ProjetoDTO {

  private ProjetoId id;
  private String titulo;
  private String descricao;
  private Long version = 0L;
  private List<TarefaDTO> tarefas = new ArrayList<>();

  public List<TarefaDTO> getTarefas() {
    return List.copyOf(tarefas);
  }

  public static ProjetoDTO from(Projeto projeto) {
    var tarefas = projeto.getTarefas().stream()
      .map(TarefaDTO::fromIgnoringCollections)
      .toList();

    var target = new ProjetoDTO();
      target.setId(projeto.getId());
      target.setTitulo(projeto.getTitulo());
      target.setDescricao(projeto.getDescricao());
      target.setVersion(projeto.getVersion());
      target.setTarefas(tarefas);
      
    return target;
  }

  public static ProjetoDTO fromIgnoringCollections(Projeto projeto) {
    var target = new ProjetoDTO();
      target.setId(projeto.getId());
      target.setTitulo(projeto.getTitulo());
      target.setDescricao(projeto.getDescricao());
      target.setVersion(projeto.getVersion());
    return target;
  }
}
