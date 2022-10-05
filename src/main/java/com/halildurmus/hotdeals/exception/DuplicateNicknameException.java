package com.halildurmus.hotdeals.exception;


public class DuplicateNicknameException extends RuntimeException {

  public DuplicateNicknameException() {
    super("This nickname is already being used!");
  }

  public DuplicateNicknameException(String message) {
    super(message);
  }

}
