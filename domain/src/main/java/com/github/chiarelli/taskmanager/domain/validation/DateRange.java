package com.github.chiarelli.taskmanager.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DateRangeConstraint.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {
    String message() default "A data deve estar entre os limites definidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String min(); // formato ISO-8601, ex: "2025-01-01T00:00"
    String max() default ""; // mesmo formato
}
