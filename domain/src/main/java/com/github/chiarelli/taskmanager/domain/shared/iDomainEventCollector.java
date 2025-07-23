package com.github.chiarelli.taskmanager.domain.shared;

public interface iDomainEventCollector {

  void collectFrom(iEventFlusher... aggregates);

}