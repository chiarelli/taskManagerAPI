package com.github.chiarelli.taskmanager.presentation.dtos;

import com.github.chiarelli.taskmanager.application.dtos.AutorDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AutorResponse {

  private final String id;
  private final String nome;

  public static AutorResponse from(AutorDTO autor) {
    return new AutorResponse(autor.getId().getId(), autor.getNome());
  }

}
