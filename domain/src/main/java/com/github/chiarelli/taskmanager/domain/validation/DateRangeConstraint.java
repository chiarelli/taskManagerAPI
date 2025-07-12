package com.github.chiarelli.taskmanager.domain.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeConstraint implements ConstraintValidator<DateRange, LocalDateTime> {

  private LocalDateTime minDate;
  private LocalDateTime maxDate;

  @Override
  public void initialize(DateRange constraintAnnotation) {

    String minRaw = constraintAnnotation.min();
    String maxRaw = constraintAnnotation.max();

    try {
      if (minRaw == null || minRaw.isBlank() || minRaw.equals("now")) {
        this.minDate = LocalDateTime.now();
      } else {
        this.minDate = LocalDateTime.parse(minRaw);
      }
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Data mínima inválida no formato ISO-8601 (yyyy-MM-dd'T'HH:mm)");
    }

    if (maxRaw == null || maxRaw.isBlank()) {
      this.maxDate = null; // sem limite superior
    } else {
      try {
        this.maxDate = LocalDateTime.parse(maxRaw);
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Data máxima inválida no formato ISO-8601 (yyyy-MM-dd'T'HH:mm)");
      }
    }
  }

  @Override
  public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
    if (value == null)
      return true; // deixe o @NotNull cuidar disso

    boolean afterMin = !value.isBefore(minDate); // value >= min
    boolean beforeMax = (maxDate == null) || !value.isAfter(maxDate); // value <= max ou sem limite

    return afterMin && beforeMax;
  }

}
