package com.github.chiarelli.taskmanager.domain.event;

import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public abstract class AbstractDomainEvent<T> {

  protected final String aggregateId;
  protected final Class<?> aggregateType;
  protected final Long aggregateVersion;

  protected final T payload;

  public AbstractDomainEvent(iDefaultAggregate aggregate, T payload) {
    this.aggregateId = aggregate.getIdAsString();
    this.aggregateType = aggregate.getClass();
    this.aggregateVersion = aggregate.getVersion();
    this.payload = payload;
  }

  public final String getAggregateId() {
    return aggregateId;
  }

  public final Class<?> getAggregateType() {
    return aggregateType;
  }

  public final Long getAggregateVersion() {
    return aggregateVersion;
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
