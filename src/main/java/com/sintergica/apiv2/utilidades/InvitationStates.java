package com.sintergica.apiv2.utilidades;

import lombok.Getter;

@Getter
public enum InvitationStates {
  ACTIVE(1, "tokenActive", ""),
  INACTIVE(2, "tokenInactive", ""),
  DIFFERENT_EMAIL(3, "emailDifferent", ""),
  VALID(4, "tokenValid", ""),
  INVALID(5, "tokenInvalid", ""),
  EXPIRED(6, "tokenExpired", "");

  private final int id;
  private final String name;
  private final String description;

  InvitationStates(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }
}
