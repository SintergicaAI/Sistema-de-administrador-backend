package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {
  private final MessagesConfig messagesConfig;
  private final InvitationRepository invitationRepository;

  /**
   * @param email The email associated with the token
   * @param signInToken The token sent to the user
   */
  public Pair<Boolean, String> validateInvitation(String email, UUID signInToken) {
    Optional<Invitation> invitation = invitationRepository.findById(signInToken);

    if (invitation.isEmpty()) {
      return new Pair<>(false, messagesConfig.getMessages().get("tokenInvalid"));
    }

    Pair<Boolean, String> validateToken =
        InvitationTokenUtils.validateToken(invitation.get(), email);

    if (!validateToken.a) {
      return new Pair<>(false, validateToken.b);
    }

    invitation.get().setActive(false);
    invitationRepository.save(invitation.get());
    return new Pair<>(true, validateToken.b);
  }
}
