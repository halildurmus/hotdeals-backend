package com.halildurmus.hotdeals.exception;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public class ExceptionResponse {

  private final LocalDateTime dateTime;
  private final int status;
  private final String error;
  private final String message;

}