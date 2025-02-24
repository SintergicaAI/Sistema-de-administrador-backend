package com.sintergica.apiv2.exceptions.user;

public class UserConflict extends RuntimeException {
  public UserConflict(String message) {
    super(message);
  }
}
