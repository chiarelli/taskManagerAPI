package com.github.chiarelli.taskmanager.domain.shared;

import java.util.List;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;

public interface iEventFlusher {

  List<AbstractDomainEvent<?>> flushEvents();

}
