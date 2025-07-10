package com.github.chiarelli.taskmanager.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;

public abstract class AbstractModelEvents {

  private List<AbstractDomainEvent<?>> events = new ArrayList<>();

  public final List<AbstractDomainEvent<?>> dumpEvents() {
    var evts = List.copyOf(events);
    this.events.clear();
    return evts;
  }

  public final void addEvent(AbstractDomainEvent<?> event) {
    events.add(event);
  }

}
