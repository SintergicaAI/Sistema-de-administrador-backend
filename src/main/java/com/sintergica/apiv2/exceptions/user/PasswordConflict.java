package com.sintergica.apiv2.exceptions.user;

public class PasswordConflict extends RuntimeException {
  public PasswordConflict(String mensaje) {
    super(mensaje);
  }
}
