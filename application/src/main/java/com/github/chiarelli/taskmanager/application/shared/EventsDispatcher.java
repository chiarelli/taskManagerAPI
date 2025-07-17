package com.github.chiarelli.taskmanager.application.shared;

import com.github.chiarelli.taskmanager.domain.shared.iDomainEventCollector;

public interface EventsDispatcher extends iDomainEventCollector {
  void emitAll();
}
