package com.halildurmus.hotdeals.exception;

public class StoreNotFoundException extends RuntimeException {

  public StoreNotFoundException() {
    super("Store not found");
  }

  public StoreNotFoundException(String message) {
    super(message);
  }
}
