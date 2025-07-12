package com.github.chiarelli.taskmanager.application.shared;

public interface EventsDispatcher extends EventCollector {
  void emitAll();
}
