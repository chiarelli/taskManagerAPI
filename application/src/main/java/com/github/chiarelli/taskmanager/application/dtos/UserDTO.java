package com.github.chiarelli.taskmanager.application.dtos;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class UserDTO {

  @EqualsAndHashCode.Include
  private UUID id;
  
  private String nome;
  private String email;
  private String papel;
  
}
