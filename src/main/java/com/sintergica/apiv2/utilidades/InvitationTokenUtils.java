package com.sintergica.apiv2.utilidades;

import com.sintergica.apiv2.entidades.Invitation;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author panther
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvitationTokenUtils {

  /**
   * Returns the state of the given token
   *
   * @param invitation Object {@code Invitation}
   * @param email Email linked to the invitation
   * @return {@code InvitationStates} to describe the token state
   */
  public static InvitationStates validateToken(Invitation invitation, String email) {

    if (!invitation.isActive()) {
      return InvitationStates.INACTIVE;
    }

    if (!invitation.getExpireDate().isBefore(LocalDateTime.now())
        && !invitation.getExpireDate().isAfter(LocalDateTime.of(2020, 1, 1, 0, 0))) {
      return InvitationStates.EXPIRED;
    }

    if (!email.equals(invitation.getEmail())) {
      return InvitationStates.DIFFERENT_EMAIL;
    }

    return InvitationStates.VALID;
  }
}
