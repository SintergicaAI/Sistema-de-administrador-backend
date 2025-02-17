package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.EmailConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.EmailUtils;
import java.time.LocalDateTime;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private final EmailConfig config;
  private final InvitationRepository invitationRepository;

  public EmailService(EmailConfig config, InvitationRepository invitationRepository) {
    this.config = config;
    this.invitationRepository = invitationRepository;
  }

  public Pair<Boolean, String> sendToken(EmailUtils.Email emailObject) {
    String FRONTEND_URL = config.getBase_url() + "/clients/register";

    emailObject.appendToBody(FRONTEND_URL + "?signInToken=" + emailObject.generateToken());
    System.out.println(emailObject);
    boolean isSuccess = EmailUtils.sendEmail(emailObject, config);

    if (!isSuccess) {
      return new Pair<>(false, "An error ocurred while sending the email");
    }

    Invitation invitation = new Invitation();
    invitation.setEmail(emailObject.getRecipients().split(",")[0]);
    invitation.setActive(true);
    invitation.setToken(emailObject.getToken());
    invitation.setExpireDate(LocalDateTime.now().plusDays(7));
    invitationRepository.save(invitation);

    System.out.println(invitationRepository.findById(emailObject.getToken()));
    return new Pair<>(true, "Sign Up email sent");
  }
}
