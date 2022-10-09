package com.halildurmus.hotdeals.exception;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${api-version}")
  private String currentApiVersion;

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<AppError> handleAuthCredentialsNotFound(
      AuthenticationCredentialsNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.UNAUTHORIZED.value()),
            "Unauthorized access of protected resource, invalid credentials",
            "auth-exceptions",
            "",
            "Unauthorized access of protected resource, invalid credentials");

    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<AppError> handleCategoryNotFound(CategoryNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "category-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<AppError> handleCommentNotFound(CommentNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "comment-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DealNotFoundException.class)
  public ResponseEntity<AppError> handleDealNotFound(DealNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "deal-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateNicknameException.class)
  public ResponseEntity<AppError> handleDuplicateNickname(DuplicateNicknameException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.BAD_REQUEST.value()),
            e.getMessage(),
            "user-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StoreNotFoundException.class)
  public ResponseEntity<AppError> handleStoreNotFound(StoreNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "store-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<AppError> handleUserNotFound(UserNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "user-exceptions",
            "",
            e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<AppError> handleConstraintViolationException(ConstraintViolationException e) {
    final List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      errors.add(violation.getPropertyPath().toString() + " " + violation.getMessage());
    }
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.BAD_REQUEST.value()),
            "Constraint violation",
            "",
            "",
            errors.toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  // TODO(halildurmus): handleHttpMessageNotWritableException

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append(e.getMethod());
    sb.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(e.getSupportedHttpMethods()).forEach(t -> sb.append(t).append(" "));

    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.METHOD_NOT_ALLOWED.value()),
            e.getMessage(),
            "",
            "",
            sb.toString());

    return handleExceptionInternal(e, error, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : e.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.BAD_REQUEST.value()),
            e.getMessage(),
            "",
            "",
            errors.toString());

    return handleExceptionInternal(e, error, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<AppError> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e, WebRequest request) {
    String errorMessage = null;
    if (e.getRequiredType() != null) {
      errorMessage = e.getName() + " should be of type " + e.getRequiredType().getName();
    }
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.BAD_REQUEST.value()),
            e.getMessage(),
            "",
            "",
            errorMessage != null ? errorMessage : e.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
    final String errorMessage =
        "No handler found for " + e.getHttpMethod() + " " + e.getRequestURL();
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            e.getMessage(),
            "",
            "",
            errorMessage);

    return handleExceptionInternal(e, error, headers, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<AppError> handleResourceNotFound(ResourceNotFoundException e) {
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.NOT_FOUND.value()),
            "Resource not found!",
            "",
            "",
            "Resource not found!");

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
    final String errorMessage =
        e.getValue()
            + " value for "
            + e.getPropertyName()
            + " should be of type "
            + e.getRequiredType();
    final AppError error =
        new AppError(
            currentApiVersion,
            Integer.toString(HttpStatus.BAD_REQUEST.value()),
            e.getMessage(),
            "",
            "",
            errorMessage);

    return handleExceptionInternal(e, error, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ValueInstantiationException.class)
  ResponseEntity<AppError> handleValueInstantiationException(
      ValueInstantiationException e, WebRequest request) {
    try {
      final Field requiredField =
          e.getType().getRawClass().getDeclaredFields()[e.getLocation().getColumnNr()];
      if (requiredField == null) {
        final String errorMessage = "Please set all corresponding fields: ".concat(e.getMessage());
        final AppError error =
            new AppError(
                currentApiVersion,
                Integer.toString(HttpStatus.BAD_REQUEST.value()),
                e.getMessage(),
                "",
                "",
                errorMessage);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      }

      final String fieldName = requiredField.getName();
      final String errorMessage =
          "Please set <"
              .concat(fieldName)
              .concat(">: ")
              .concat(fieldName.concat(" is required field"));
      final AppError error =
          new AppError(
              currentApiVersion,
              Integer.toString(HttpStatus.BAD_REQUEST.value()),
              e.getMessage(),
              "",
              "",
              errorMessage);

      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    } catch (Exception ex) {
      final String errorMessage = "Please set all corresponding fields!";
      final AppError error =
          new AppError(
              currentApiVersion,
              Integer.toString(HttpStatus.BAD_REQUEST.value()),
              e.getMessage(),
              "",
              "",
              errorMessage);

      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
  }
}
