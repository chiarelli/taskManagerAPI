package com.github.chiarelli.taskmanager.application.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.shared.EventsDispatcher;
import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;
import com.github.chiarelli.taskmanager.domain.shared.iEventFlusher;

import io.github.jkratz55.mediator.core.Mediator;

@Component
public class DomainEventsDispatcher implements EventsDispatcher {

  private List<AbstractDomainEvent<?>> events = new ArrayList<>();
  private final Mediator eventPublisher;

  public DomainEventsDispatcher(Mediator eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void collectFrom(iEventFlusher... aggregates) {
    Objects.requireNonNull(aggregates);
    
    Arrays.stream(aggregates)
      .filter(Objects::nonNull)
      .map(iEventFlusher::flushEvents)
      .flatMap(Collection::stream)
      .forEach(events::add);
  }

  @Override
  public void emitAll() {
    events.stream()
      .map(DomainEventAdapters::adapt)
      .forEach(eventPublisher::emit);
    events.clear();
  }

}
