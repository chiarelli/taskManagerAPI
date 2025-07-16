package com.github.chiarelli.taskmanager.domain.shared;

import com.github.chiarelli.taskmanager.domain.model.BaseModel;

public interface iDomainEventCollector {

  void collectFrom(BaseModel... aggregates);

}