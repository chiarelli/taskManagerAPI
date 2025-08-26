package com.github.chiarelli.taskmanager.application.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class UserDTO {
  @EqualsAndHashCode.Include
  private UUID id;
  
  private String username;
  private String role;
  
}
