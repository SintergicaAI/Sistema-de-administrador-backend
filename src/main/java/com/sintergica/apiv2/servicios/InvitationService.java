package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.EmailConfig;
import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.InvitationStates;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;
import com.sintergica.apiv2.utilidades.email.Email;
import com.sintergica.apiv2.utilidades.email.EmailUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
  private final EmailConfig config;
  private final MessagesConfig messagesConfig;
  private final InvitationRepository invitationRepository;
  private final HashMap<InvitationStates, String> invalidInvitationStates =
      new HashMap<>() {
        {
          put(InvitationStates.INVALID, "tokenInvalid");
          put(InvitationStates.EXPIRED, "tokenExpired");
          put(InvitationStates.DIFFERENT_EMAIL, "emailDifferent");
        }
      };

  /**
   * Stores the token into the database
   *
   * @param email The email associated with the token
   * @param token The token to store into the database
   */
  private void storeInvitation(String email, UUID token) {
    Invitation invitation = new Invitation();
    invitation.setEmail(email);
    invitation.setActive(true);
    invitation.setToken(token);
    invitation.setExpireDate(LocalDateTime.now().plusDays(7));
    invitationRepository.save(invitation);
  }

  /**
   * Mark an invitation as inActive
   *
   * @param email The email associated with the invitation token
   */
  private void inactiveInvitation(String email) {
    Invitation invitation = new Invitation();
    invitation.setEmail(email);
    invitation.setActive(false);
    invitationRepository.save(invitation);
  }

  /**
   * Mark an invitation as inActive
   *
   * @param token The invitation token
   */
  private void inactiveInvitation(UUID token) {
    Invitation invitation = new Invitation();
    invitation.setToken(token);
    invitation.setActive(false);
    invitationRepository.save(invitation);
  }

  /**
   * Sends an invitation token to a selected user
   *
   * @param emailObject The abstracted email with the token
   * @return {@code true} if token was sent successfully or {@code false} if token wasn't sent
   */
  private Boolean sendToken(Email emailObject) {
    final String FRONTEND_URL = config.getBase_url() + "/users/register";
    emailObject.getMessage().appendToBody(FRONTEND_URL + "?signInToken=" + emailObject.getToken());
    boolean isSuccess = EmailUtils.sendEmail(emailObject, config);

    if (!isSuccess) {
      return false;
    }

    storeInvitation(emailObject.getMessage().getRecipients().split(",")[0], emailObject.getToken());

    return true;
  }

  /**
   * Returns {@code true} if exist one invitation associated to the provided email
   *
   * @param email - The email associated to an invitation
   * @return {@code true} if exists an invitation or {@code false} if doesn't exist
   */
  private Boolean existInvitation(String email) {
    Optional<Invitation> invitation = invitationRepository.findByEmail(email);
    return invitation.isPresent();
  }

  /**
   * @return {@code List<Invitation>} with all invitations
   */
  public List<Invitation> invitationList() {
    return invitationRepository.findAll();
  }

  /**
   * Sends a invitation token via email
   *
   * @param emailObject The abstracted email with the token
   * @return {@code true} if token was sent successfully or {@code false} if token wasn't sent
   */
  public Boolean sendNewToken(Email emailObject) {
    if (!existInvitation(emailObject.getMessage().getRecipients())) {
      emailObject.generateToken();
      return sendToken(emailObject);
    }

    return resendToken(emailObject.getMessage().getRecipients());
  }

  /**
   * If exists in the database, send the same token again, else send a new token and stores it in
   * the database
   *
   * @param email The email associated with the token
   * @return {@code true} if token was sent successfully or {@code false} if token wasn't sent
   */
  public Boolean resendToken(String email) {
    Optional<Invitation> invitation = invitationRepository.findByEmail(email);

    Email emailObject = new Email();

    if (invitation.isEmpty()) {
      emailObject.generateToken();
      sendNewToken(emailObject);
      return false;
    }

    emailObject.getMessage().setRecipients(email);
    emailObject.setToken(invitation.get().getToken());
    sendToken(emailObject);

    return true;
  }

  /**
   * Returns {@code true} if the specified token is valid and uses the token making it inactive
   *
   * @param email The email which wants to consume an invitation token
   * @param signInToken The token sent to the user
   * @return {@code false} if invitation is invalid or {@code true} if invitation is valid and its
   *     corresponding message
   */
  public Pair<Boolean, String> consumeInvitation(String email, UUID signInToken) {
    Optional<Invitation> invitation = invitationRepository.findById(signInToken);

    if (invitation.isEmpty()) {
      return new Pair<>(false, messagesConfig.getMessages().get("tokenInvalid"));
    }

    InvitationStates validateToken = InvitationTokenUtils.validateToken(invitation.get(), email);

    if (invalidInvitationStates.containsKey(validateToken)) {
      return new Pair<>(
          false, messagesConfig.getMessages().get(invalidInvitationStates.get(validateToken)));
    }

    invitation.get().setActive(false);
    invitationRepository.save(invitation.get());
    return new Pair<>(true, messagesConfig.getMessages().get("tokenValid"));
  }

  /**
   * Returns {@code true} if the specified token is valid
   *
   * @param email The email which wants to consume an invitation token
   * @param invitationToken The token sent to the user
   * @return {@code false} if invitation is invalid or {@code true} if invitation is valid
   */
  public Boolean validateInvitation(String email, UUID invitationToken) {
    Optional<Invitation> invitation = invitationRepository.findById(invitationToken);
    return invitation
        .filter(
            value ->
                InvitationTokenUtils.validateToken(value, email).equals(InvitationStates.VALID))
        .isPresent();
  }

  /**
   * Returns {@code true} if the specified email has a valid invitation
   *
   * @param email The email which wants to consume an invitation token
   * @return {@code false} if email has an invalid invitation or {@code true} if invitation is valid
   */
  public Boolean validateInvitation(String email) {
    Optional<Invitation> invitation = invitationRepository.findByEmail(email);
    return invitation
        .filter(
            value ->
                InvitationTokenUtils.validateToken(value, email).equals(InvitationStates.VALID))
        .isPresent();
  }

  /**
   * Returns {@code true} if the specified token is valid
   *
   * @param invitationToken The token sent to the user
   * @return {@code false} if invitation is invalid or {@code true} if invitation is valid
   */
  public Boolean validateInvitation(UUID invitationToken) {
    Optional<Invitation> invitation = invitationRepository.findById(invitationToken);
    return invitation
        .filter(value -> InvitationTokenUtils.validateToken(value).equals(InvitationStates.VALID))
        .isPresent();
  }

  /**
   * @param invitationToken The token associated to the email
   * @return {@code true} if successfully deleted the invitation, otherwise {@code false}
   */
  public Boolean deleteInvitation(UUID invitationToken) {
    // inactiveInvitation(invitationToken);

    Optional<Invitation> invitation = invitationRepository.findById(invitationToken);
    if (invitation.isEmpty()) {
      return false;
    }

    invitationRepository.delete(invitation.get());
    return true;
  }
}
