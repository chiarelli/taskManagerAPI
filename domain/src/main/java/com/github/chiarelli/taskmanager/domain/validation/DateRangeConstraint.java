package com.github.chiarelli.taskmanager.domain.validation;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeConstraint implements ConstraintValidator<DateRange, Date> {

  private String minRaw;
  private String maxRaw;

  @Override
  public void initialize(DateRange constraintAnnotation) {
    this.minRaw = constraintAnnotation.min();
    this.maxRaw = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(Date value, ConstraintValidatorContext context) {
    if (value == null)
      return true;

    Instant valueInstant = value.toInstant();

    Instant minInstant = parseDate(minRaw, true);
    Instant maxInstant = parseDate(maxRaw, false);

    boolean afterMin = !valueInstant.isBefore(minInstant);
    boolean beforeMax = (maxInstant == null) || !valueInstant.isAfter(maxInstant);

    return afterMin && beforeMax;
  }

  private Instant parseDate(String raw, boolean isMin) {
    if (raw == null || raw.isBlank()) {
      return isMin ? Instant.now() : Instant.MAX;
    }

    if (raw.toLowerCase().contains("now")) {
      return parseRelativeDate(raw);
    }

    try {
      // Tenta parsear ISO 8601 como Instant
      return Instant.parse(raw);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Data inválida no formato ISO-8601: " + raw);
    }
  }

  private Instant parseRelativeDate(String raw) {
    Instant now = Instant.now();

    if (raw.equalsIgnoreCase("now")) {
      return now;
    }

    // Ex: "now+1d", "now-2w", "now+3h"
    Pattern p = Pattern.compile("now([+-])(\\d+)([dwhms])");
    Matcher m = p.matcher(raw.replaceAll("\\s+", ""));

    if (m.matches()) {
      String op = m.group(1); // + ou -
      long amount = Long.parseLong(m.group(2));
      String unit = m.group(3);

      ChronoUnit chronoUnit = switch (unit) {
        case "d" -> ChronoUnit.DAYS;
        case "w" -> ChronoUnit.WEEKS;
        case "h" -> ChronoUnit.HOURS;
        case "m" -> ChronoUnit.MINUTES;
        case "s" -> ChronoUnit.SECONDS;
        default -> throw new IllegalArgumentException("Unidade inválida: " + unit);
      };

      return op.equals("+") ? now.plus(amount, chronoUnit) : now.minus(amount, chronoUnit);
    }

    throw new IllegalArgumentException("Formato inválido: " + raw);
  }
}
