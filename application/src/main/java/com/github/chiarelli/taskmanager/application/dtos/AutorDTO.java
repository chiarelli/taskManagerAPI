package com.github.chiarelli.taskmanager.application.dtos;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AutorDTO {

  private final AutorId id;
  private final String nome;

}
