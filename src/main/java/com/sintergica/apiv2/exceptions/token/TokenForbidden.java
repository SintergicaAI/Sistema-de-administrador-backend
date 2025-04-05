package com.sintergica.apiv2.exceptions.token;

public class TokenForbidden extends RuntimeException {

  public TokenForbidden(String msg) {
    super(msg);
  }
}
