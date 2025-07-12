package com.github.chiarelli.taskmanager.domain.validation;

import java.util.Set;

import com.github.chiarelli.taskmanager.domain.exception.DomainException;

import jakarta.validation.ConstraintViolation;

public interface iDomainValidator<T> {

  Set<ConstraintViolation<T>> validate();

  void assertValid() throws DomainException;

}
