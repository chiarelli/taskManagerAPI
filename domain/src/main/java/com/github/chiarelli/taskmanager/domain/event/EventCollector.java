package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.AbstractModelEvents;

public interface EventCollector {

  void collectFrom(AbstractModelEvents... aggregates);

}