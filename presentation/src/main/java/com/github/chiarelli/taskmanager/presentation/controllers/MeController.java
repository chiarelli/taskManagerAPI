package com.github.chiarelli.taskmanager.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.chiarelli.taskmanager.application.dtos.UserDTO;


@RestController
@RequestMapping("/api/v1/me")
public class MeController {

  @GetMapping()
  public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserDTO userLogged) {
    return ResponseEntity.ok(userLogged);
  }
  
}
