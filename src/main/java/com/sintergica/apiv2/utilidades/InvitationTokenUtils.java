package com.sintergica.apiv2.utilidades;

import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.entidades.Invitation;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvitationTokenUtils {

  public static Pair<Boolean, String> validateToken(Invitation invitation, String email) {
    MessagesConfig messagesConfig = new MessagesConfig();

    if (!invitation.isActive()) {
      return new Pair<>(false, messagesConfig.getMessages().get("tokenUsed"));
    }

    if (!invitation.getExpireDate().isBefore(LocalDateTime.now())
        && !invitation.getExpireDate().isAfter(LocalDateTime.of(2020, 1, 1, 0, 0))) {
      return new Pair<>(true, messagesConfig.getMessages().get("tokenExpired"));
    }

    if (!email.equals(invitation.getEmail())) {
      return new Pair<>(false, messagesConfig.getMessages().get("emailDifferent"));
    }

    return new Pair<>(true, messagesConfig.getMessages().get("tokenValid"));
  }
}
