package com.github.chiarelli.taskmanager.domain.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.chiarelli.taskmanager.domain.exception.DomainException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class GenericValidator<T> implements iDomainValidator<T> {

  private final T instance;

  private static final Validator validator;

  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  public GenericValidator(T instance) {
    this.instance = instance;
  }

  @Override
  public Set<ConstraintViolation<T>> validate() {
    return validator.validate(instance);
  }

  @Override
  public void assertValid() throws DomainException {
    var violations = validate();
    if (violations.size() > 0) {
      Map<String, Object> messages = new HashMap<>();

      for (ConstraintViolation<T> violation : violations) {
        messages.put(violation.getPropertyPath().toString(), violation.getMessage());
      }
      throw new DomainException(messages);
    }
  }

}
