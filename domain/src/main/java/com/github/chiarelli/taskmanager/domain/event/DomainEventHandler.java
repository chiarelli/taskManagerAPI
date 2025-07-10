package com.github.chiarelli.taskmanager.domain.event;

public interface DomainEventHandler<T extends AbstractDomainEvent<?>> {
  void handle(T event);
}
