package com.github.chiarelli.taskmanager.domain.exception;

public class CommandAlreadyProcessedException extends RuntimeException {
  public CommandAlreadyProcessedException(String message) {
    super(message);
  }
}