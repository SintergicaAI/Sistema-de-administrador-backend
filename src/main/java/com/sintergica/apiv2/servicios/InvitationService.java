package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.InvitationStates;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

/**
 * @author panther
 */
@Service
@RequiredArgsConstructor
public class InvitationService {
  private final MessagesConfig messagesConfig;
  private final InvitationRepository invitationRepository;
  private final HashMap<InvitationStates,String> invalidInvitationStates = new HashMap<>(){
    {
      put(InvitationStates.INVALID,"tokenInvalid");
      put(InvitationStates.EXPIRED,"tokenExpired");
      put(InvitationStates.DIFFERENT_EMAIL,"emailDifferent");
    }
  };

  /**
   * Returns {@code true} if the specified token is valid and uses the token making it inactive
   * @param email The email associated with the token
   * @param signInToken The token sent to the user
   * @return {@code false} if invitation is invalid or {@code true} if invitation is valid and its corresponding message
   */
  public Pair<Boolean, String> consumeInvitation(String email, UUID signInToken) {
    Optional<Invitation> invitation = invitationRepository.findById(signInToken);

    if (invitation.isEmpty()) {
      return new Pair<>(false, messagesConfig.getMessages().get("tokenInvalid"));
    }

    InvitationStates validateToken = InvitationTokenUtils.validateToken(invitation.get(), email);

    if (invalidInvitationStates.containsKey(validateToken)){
      return new Pair<>(false, messagesConfig.getMessages().get(
              invalidInvitationStates.get(validateToken)
      ));
    }

    invitation.get().setActive(false);
    invitationRepository.save(invitation.get());
    return new Pair<>(true, messagesConfig.getMessages().get("tokenValid"));
  }

  /**
   * Returns {@code true} if the specified token is valid
   * @param email The email associated with the token
   * @param invitationToken The token sent to the user
   * @return {@code false} if invitation is invalid or {@code true} if invitation is valid
   */
  public Boolean validateInvitation(String email, UUID invitationToken){
    Optional<Invitation> invitation = invitationRepository.findById(invitationToken);

    if (invitation.isEmpty()) {
      return false;
    }

    InvitationStates validateToken = InvitationTokenUtils.validateToken(invitation.get(), email);

    if (!validateToken.equals(InvitationStates.VALID)) {
      return false;
    }

    return true;
  }
}
