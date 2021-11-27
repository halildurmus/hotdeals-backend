package com.halildurmus.hotdeals.exception;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundException e) {
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(e.getLocalizedMessage()).message(e.getMessage()).build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  ResponseEntity<ExceptionResponse> handleUserNotFound(UserNotFoundException e) {
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase()).message(e.getMessage()).build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  ResponseEntity<ExceptionResponse> handleDealNotFound(DealNotFoundException e) {
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase()).message(e.getMessage()).build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    final List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      errors.add(violation.getPropertyPath().toString() + violation.getMessage());
    }

    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
        .message(errors.toString()).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e,
      WebRequest request) {
    final String errorMessage =
        e.getName() + " should be of type " + e.getRequiredType().getName();
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(e.getLocalizedMessage()).message(errorMessage).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  ResponseEntity<Object> handleValueInstantiationException(ValueInstantiationException e,
      WebRequest request) {
    try {
      Field requiredField = e.getType().getRawClass().getDeclaredFields()[e.getLocation()
          .getColumnNr()];
      if (requiredField == null) {
        final String errorMessage = "Please set all corresponding fields: ".concat(e.getMessage());
        final ExceptionResponse response = ExceptionResponse.builder()
            .dateTime(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(e.getLocalizedMessage()).message(errorMessage).build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      final String fieldName = requiredField.getName();
      final String errorMessage = "Please set <".concat(fieldName).concat(">: ")
          .concat(fieldName.concat(" is required field"));
      final ExceptionResponse response = ExceptionResponse.builder()
          .dateTime(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
          .message(errorMessage).build();

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    } catch (Exception ex) {
      final String errorMessage = "Please set all corresponding fields!";
      final ExceptionResponse response = ExceptionResponse.builder()
          .dateTime(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value()).error(ex.getLocalizedMessage())
          .message(errorMessage).build();

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : e.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }

    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
        .message(errors.toString()).build();

    return handleExceptionInternal(e, response, headers, HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(BindException e, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : e.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
        .message(errors.toString()).build();

    return handleExceptionInternal(e, response, headers, HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    final String errorMessage =
        e.getValue() + " value for " + e.getPropertyName() + " should be of type " + e
            .getRequiredType();
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
        .message(errorMessage).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException e, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    final String errorMessage = e.getRequestPartName() + " part is missing";
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(e.getLocalizedMessage())
        .message(errorMessage).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException e, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    final String errorMessage = e.getParameterName() + " parameter is missing";
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(e.getLocalizedMessage()).message(errorMessage).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    final String errorMessage =
        "No handler found for " + e.getHttpMethod() + " " + e.getRequestURL();
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(e.getLocalizedMessage()).message(errorMessage).build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append(e.getMethod());
    sb.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(e.getSupportedHttpMethods()).forEach(t -> sb.append(t).append(" "));

    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
        .error(e.getLocalizedMessage()).message(sb.toString()).build();

    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException e, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append(e.getContentType());
    sb.append(" media type is not supported. Supported media types are ");
    e.getSupportedMediaTypes().forEach(t -> sb.append(t).append(" "));

    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .error(e.getLocalizedMessage()).message(sb.substring(0, sb.length() - 2)).build();

    return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(e.getCause().getLocalizedMessage()).message(e.getCause().getMessage()).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  ResponseEntity<ExceptionResponse> handleException(Exception e) {
    final ExceptionResponse response = ExceptionResponse.builder()
        .dateTime(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(e.getLocalizedMessage()).message(e.getMessage()).build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}