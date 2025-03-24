package com.sintergica.apiv2.exceptions.user;

public class EmailWrong extends RuntimeException {
  public EmailWrong(String message) {
    super(message);
  }
}
