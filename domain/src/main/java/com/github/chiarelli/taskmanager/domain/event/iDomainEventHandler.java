package com.github.chiarelli.taskmanager.domain.event;

public interface iDomainEventHandler<T extends AbstractDomainEvent<?>> {
  void handle(T event);
}
