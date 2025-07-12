package com.github.chiarelli.taskmanager.application.shared;

public interface EventHandler<E extends Event> extends io.github.jkratz55.mediator.core.EventHandler<E> {
  void handle(E event);
}
