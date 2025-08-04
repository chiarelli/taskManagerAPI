package com.github.chiarelli.taskmanager.application.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class UserDTO {

  private UUID id;
  private String nome;
  private String email;
  private String papel;
  
}
