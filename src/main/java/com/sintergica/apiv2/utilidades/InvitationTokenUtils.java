package com.sintergica.apiv2.utilidades;

import com.sintergica.apiv2.entidades.Invitation;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;

@Data
@RequiredArgsConstructor
public final class InvitationTokenUtils {

  public static Pair<Boolean, String> validateToken(Invitation invitation, String email) {

    if (!invitation.isActive()) {
      return new Pair<>(false, "SignIn Token Used");
    }

    if (!invitation.getExpireDate().isBefore(LocalDateTime.now())
        && !invitation.getExpireDate().isAfter(LocalDateTime.of(2020, 1, 1, 0, 0))) {
      return new Pair<>(true, "SignIn Token Expired");
    }

    if (!email.equals(invitation.getEmail())) {
      return new Pair<>(false, "Your email isn't the same");
    }

    return new Pair<>(true, "Valid token");
  }
}
