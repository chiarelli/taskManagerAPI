package com.github.chiarelli.taskmanager.domain.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeConstraint implements ConstraintValidator<DateRange, LocalDateTime> {

  private String minRaw;
  private String maxRaw;

  @Override
  public void initialize(DateRange constraintAnnotation) {
    this.minRaw = constraintAnnotation.min();
    this.maxRaw = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
    if (value == null)
      return true;

    LocalDateTime minDate = parseDate(minRaw, true);
    LocalDateTime maxDate = parseDate(maxRaw, false);

    boolean afterMin = !value.isBefore(minDate);
    boolean beforeMax = (maxDate == null) || !value.isAfter(maxDate);

    return afterMin && beforeMax;
  }

  private LocalDateTime parseDate(String raw, boolean isMin) {
    if (raw == null || raw.isBlank()) {
      return isMin ? LocalDateTime.now() : null;
    }
    if (raw.equalsIgnoreCase("now")) {
      return LocalDateTime.now();
    }
    try {
      return LocalDateTime.parse(raw);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Data inválida no formato ISO-8601: " + raw);
    }
  }
}
