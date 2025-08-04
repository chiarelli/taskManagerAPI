package com.github.chiarelli.taskmanager.presentation.handlers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.presentation.dtos.BadRequestResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class APIRestExceptionsHandler {

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<BadRequestResponse> handleUIException(DomainException ex) {
    var badResp = new BadRequestResponse(ex.getViolations());
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, Object> body = Map.of(
        "status", 400,
        "message", "Parâmetros inválidos",
        "erros", ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .toList());
            
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<BadRequestResponse> handleNotFoundException(NotFoundException ex) {
    var badResp = new BadRequestResponse(Map.of("not_found", ex.getMessage()));
    return new ResponseEntity<>(badResp, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BadRequestResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    var badResp = new BadRequestResponse(Map.of("request", "Parâmetros inválidos foram enviados na requisição."));
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<BadRequestResponse> handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
    String rawMessage = ex.getMessage();
    String id = extractEntityId(rawMessage);
    
    Map<String, Object> body = new HashMap<>();
    body.put("conflito", "A entidade com ID %s foi modificada por outro processo. Atualize os dados e tente novamente.".formatted(id));
    
    var badResp = new BadRequestResponse(body);
    return new ResponseEntity<>(badResp, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(com.github.chiarelli.taskmanager.domain.exception.OptimisticLockingFailureException.class)
  public ResponseEntity<BadRequestResponse> handleDomainOptimisticLockingFailure(com.github.chiarelli.taskmanager.domain.exception.OptimisticLockingFailureException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("conflito", ex.getMessage());
    
    var badResp = new BadRequestResponse(body);
    return new ResponseEntity<>(badResp, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    var cause = ex.getCause();

    if (cause instanceof JsonParseException jpe) {
      return handleJsonParseException(jpe);

    } else if (cause instanceof InvalidFormatException ife) {
      return handleInvalidFormatException(ife);

    } else if (cause instanceof MismatchedInputException mie) {
      return handleMismatchedInputException(mie);
    }

    return handleThrowable(cause); // fallback genérico
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BadRequestResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String field = error.getField();
      String message = error.getDefaultMessage();
      errors.put(field, message);
    });

    var badResp = new BadRequestResponse(errors);
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(JsonParseException.class)
  public ResponseEntity<BadRequestResponse> handleJsonParseException(JsonParseException ex) {
    var badResp = new BadRequestResponse(Map.of("invalid_json", "JSON inválido"));
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<BadRequestResponse> handleInvalidFormatException(InvalidFormatException ex) {
    String fieldName = ex.getPath().stream()
        .map(ref -> ref.getFieldName())
        .reduce((a, b) -> b)
        .orElse("valor");

    String msg;

    // Detecção do tipo alvo para personalizar mensagem
    Class<?> targetType = ex.getTargetType();
    if (targetType != null && targetType.equals(LocalDateTime.class)) {
      msg = "Data e hora devem estar no formato ISO 8601 (ex: yyyy-MM-dd'T'HH:mm:ss)";
    } else if (targetType != null && targetType.isEnum()) {
      msg = "Valor inválido. Use um dos valores permitidos: " +
              Arrays.toString(targetType.getEnumConstants());
    } else {
      msg = "Formato de valor inválido para o campo";
    }

    var badResp = new BadRequestResponse(Map.of(fieldName, msg));
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<BadRequestResponse> handleMismatchedInputException(MismatchedInputException ex) {
    String fieldName = ex.getPath().stream()
        .map(ref -> ref.getFieldName())
        .reduce((a, b) -> b)
        .orElse("valor");

    var badResp = new BadRequestResponse(Map.of(fieldName, "Formato de valor inválido"));
    return new ResponseEntity<>(badResp, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<Map<String, Object>> handleThrowable(Throwable ex) {
    // TODO Logue para investigação (log completo no back-end)
    ex.printStackTrace();

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "error", "Erro interno no servidor. Tente novamente mais tarde."));
  }

  private String extractEntityId(String message) {
      Pattern pattern = Pattern.compile("entity\\s+([\\w\\-]+)\\s+with\\s+version");
      Matcher matcher = pattern.matcher(message);
      if (matcher.find()) {
          return matcher.group(1); // retorna o UUID
      }
      return "desconhecido"; // fallback
  }
  
}
