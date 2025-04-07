package com.sintergica.apiv2.exceptions.group;

public class GroupConflict extends RuntimeException {
  public GroupConflict(String message) {
    super(message);
  }

  public GroupConflict(String message, Throwable cause) {
    super(message, cause);
  }
}
