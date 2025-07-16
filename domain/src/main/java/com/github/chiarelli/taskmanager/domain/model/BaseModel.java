package com.github.chiarelli.taskmanager.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.shared.iEventFlusher;

public class BaseModel implements iEventFlusher {

  private final List<AbstractDomainEvent<?>> events = new ArrayList<>();

  protected void addEvent(AbstractDomainEvent<?> event) {
    events.add(event);
  }

  @Override
  public List<AbstractDomainEvent<?>> flushEvents() {
    List<AbstractDomainEvent<?>> result = List.copyOf(events);
    events.clear();
    return result;
  }

}
