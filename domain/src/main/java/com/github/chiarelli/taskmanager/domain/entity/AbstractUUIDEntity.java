package com.github.chiarelli.taskmanager.domain.entity;

import java.util.UUID;

public abstract class AbstractUUIDEntity {

  private UUID id;

  public AbstractUUIDEntity(UUID id) {
    this.id = id;
  }

  public AbstractUUIDEntity() {
    this.id = UUID.randomUUID();
  }

  public UUID getId() {
    return id;
  }

  @Override
  public String toString() {
    return id.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    AbstractUUIDEntity that = (AbstractUUIDEntity) obj;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}
