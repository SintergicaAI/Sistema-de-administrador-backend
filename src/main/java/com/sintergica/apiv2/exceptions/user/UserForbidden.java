package com.sintergica.apiv2.exceptions.user;

public class UserForbidden extends RuntimeException {

  public UserForbidden(String msg) {
    super(msg);
  }
}
