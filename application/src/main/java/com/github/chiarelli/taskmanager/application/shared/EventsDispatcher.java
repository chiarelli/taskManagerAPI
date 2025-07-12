package com.github.chiarelli.taskmanager.application.shared;

import com.github.chiarelli.taskmanager.domain.event.EventCollector;

public interface EventsDispatcher extends EventCollector {
  void emitAll();
}
