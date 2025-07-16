package com.github.chiarelli.taskmanager.domain.dto;

import java.util.List;

import com.github.chiarelli.taskmanager.domain.event.AbstractDomainEvent;

public record ServiceResult<T>(
  T result,
  List<AbstractDomainEvent<?>> events
) { }
