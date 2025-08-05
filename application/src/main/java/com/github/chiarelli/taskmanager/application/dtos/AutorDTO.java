package com.github.chiarelli.taskmanager.application.dtos;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class AutorDTO {

  @EqualsAndHashCode.Include
  private final AutorId id;
  
  private final String nome;

}
