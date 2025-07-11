package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public abstract class AbstractDomainEvent<T> {

  protected final iDefaultAggregate aggregate;
  protected final T payload;

  public AbstractDomainEvent(iDefaultAggregate aggregate, T payload) {
    this.aggregate = aggregate;
    this.payload = payload;
  }

  public final iDefaultAggregate getAggregate() {
    return aggregate;
  }
  
  public T getPayload() {
    return payload;
  }
  
  public final String getEventType() {
    return this.getClass().getSimpleName();
  }
  
  public final String getEventName() {
    return this.getClass().getName();
  }

}
