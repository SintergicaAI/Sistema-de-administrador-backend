package com.sintergica.apiv2.exceptions.user;

public class UserNotFound extends RuntimeException {

  public UserNotFound(String message) {
    super(message);
  }
}
