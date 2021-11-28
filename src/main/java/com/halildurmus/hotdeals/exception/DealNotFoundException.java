package com.halildurmus.hotdeals.exception;

public class DealNotFoundException extends RuntimeException {

  public DealNotFoundException() {
    super("Deal not found");
  }

  public DealNotFoundException(String message) {
    super(message);
  }

}
