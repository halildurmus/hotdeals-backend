package com.halildurmus.hotdeals.exception;

public class DealNotFoundException extends RuntimeException {

  public DealNotFoundException() {
    super("Deal not found");
  }

  public DealNotFoundException(String message) {
    super(message);
  }

  public DealNotFoundException(Throwable cause) {
    super(cause);
  }

  public DealNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public DealNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
