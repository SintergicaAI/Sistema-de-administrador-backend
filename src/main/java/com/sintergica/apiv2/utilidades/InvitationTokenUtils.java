package com.sintergica.apiv2.utilidades;

import com.sintergica.apiv2.entidades.Invitation;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvitationTokenUtils {

  public static HashMap<String, Object> validateToken(Invitation invitation, String email) {
    HashMap<String, Object> response = new HashMap<>();

    response.put("isValid", false);

    if (!invitation.isActive()) {
      response.put("message", "SignIn Token Used");
      return response;
    }

    if (!invitation.getExpireDate().isBefore(LocalDateTime.now())
        && !invitation.getExpireDate().isAfter(LocalDateTime.of(2020, 1, 1, 0, 0))) {
      response.put("message", "SignIn Token Expired");
      return response;
    }

    if (!email.equals(invitation.getEmail())) {
      response.put("message", "Your email isn't the same");
      return response;
    }

    response.put("isValid", true);

    return response;
  }
}
