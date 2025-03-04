package com.sintergica.apiv2.exceptions.role;

public class RoleNotAllowedInGroupException extends RuntimeException {
  public RoleNotAllowedInGroupException(String message) {
    super(message);
  }
}
