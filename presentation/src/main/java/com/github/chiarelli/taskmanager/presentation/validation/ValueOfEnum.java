package com.github.chiarelli.taskmanager.presentation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueOfEnum {
  Class<? extends Enum<?>> enumClass();
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  
  String message() default "Valor inválido para o campo %s. Os valores aceitos são: %s";
  String fieldName();
}
