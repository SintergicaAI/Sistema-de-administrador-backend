package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.EmailConfig;
import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.EmailUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final EmailConfig config;
  private final MessagesConfig messagesConfig;
  private final InvitationRepository invitationRepository;

  public Pair<Boolean, String> sendToken(EmailUtils.Email emailObject) {
    String FRONTEND_URL = config.getBase_url() + "/clients/register";

    emailObject.appendToBody(FRONTEND_URL + "?signInToken=" + emailObject.generateToken());
    boolean isSuccess = EmailUtils.sendEmail(emailObject, config);

    if (!isSuccess) {
      return new Pair<>(false, messagesConfig.getMessages().get("emailSendError"));
    }

    Invitation invitation = new Invitation();
    invitation.setEmail(emailObject.getRecipients().split(",")[0]);
    invitation.setActive(true);
    invitation.setToken(emailObject.getToken());
    invitation.setExpireDate(LocalDateTime.now().plusDays(7));
    invitationRepository.save(invitation);

    return new Pair<>(true, messagesConfig.getMessages().get("emailSent"));
  }
}
