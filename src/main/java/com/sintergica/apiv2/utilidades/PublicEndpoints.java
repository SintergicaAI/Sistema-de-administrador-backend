package com.sintergica.apiv2.utilidades;

public final class PublicEndpoints {

  public static final String[] publicEndpoints = {
          "/users/login",
          "/users/register",
          "/users/logout",
          "/users/refreshToken",
          "/users/updateTokens",
          "/invitation/validate",
          "/users/change-password"
          ,"/users/forgot-password"
  };

  private PublicEndpoints() {}
}
