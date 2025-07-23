package com.github.chiarelli.taskmanager.domain.dto;

import java.util.List;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.shared.iEventFlusher;

public class ServiceResult<T> implements iEventFlusher {
  private T result;
  private List<AbstractDomainEvent<?>> events;

  public ServiceResult(T result, List<AbstractDomainEvent<?>> events) {
    this.result = result;
    this.events = events;
  }

  @Override
  public List<AbstractDomainEvent<?>> flushEvents() {
    List<AbstractDomainEvent<?>> result = List.copyOf(events);
    events.clear();
    return result;
  }
  
  public T result() {
    return result;
  }
  
}
