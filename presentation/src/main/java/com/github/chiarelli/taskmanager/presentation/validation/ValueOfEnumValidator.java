package com.github.chiarelli.taskmanager.presentation.validation;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {

  private String[] acceptedValues;
  private String acceptedValuesFormatted;
  
  private String fieldName;
  private String messageF;

  @Override
  public void initialize(ValueOfEnum annotation) {
    acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
        .map(Enum::name)
        .toArray(String[]::new);
    
    acceptedValuesFormatted = String.join(", ", acceptedValues);

    fieldName = annotation.fieldName();
    messageF = annotation.message();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return true; // Deixe o @NotNull cuidar disso

    boolean isValid = Arrays.asList(acceptedValues).contains(value);
    
    if(isValid)
      return true;

    // Desativa a mensagem padrão e constrói uma nova
    context.disableDefaultConstraintViolation();

    context.buildConstraintViolationWithTemplate(
        messageF.formatted(fieldName, acceptedValuesFormatted)
    ).addConstraintViolation();

    return false;
  }
}