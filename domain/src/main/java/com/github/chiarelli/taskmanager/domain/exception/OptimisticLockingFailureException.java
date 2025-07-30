package com.github.chiarelli.taskmanager.domain.exception;

public class OptimisticLockingFailureException extends RuntimeException {
  public OptimisticLockingFailureException(String message) {
    super(message);
  }
}
