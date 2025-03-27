package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.dto.EmailDTO;
import com.sintergica.apiv2.dto.InvitationDTO;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.servicios.InvitationService;
import com.sintergica.apiv2.utilidades.email.Email;
import com.sintergica.apiv2.utilidades.email.Message;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author panther
 */
@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
public class InvitationController {
  private final MessagesConfig messagesConfig;
  private final InvitationService invitationService;

  @GetMapping
  public List<Invitation> getAllInvitations() {
    return invitationService.invitationList();
  }

  @PostMapping("/send")
  public ResponseEntity<HashMap<String, Object>> sendInvitationEmail(
      @RequestBody EmailDTO emailObject) {
    HashMap<String, Object> response = new HashMap<>();

    Email email =
        new Email(
            emailObject.getFromEmail(),
            emailObject.getEmailPassword(),
            emailObject.getToken(),
            new Message(
                emailObject.getSubject(), emailObject.getBody(), emailObject.getRecipients()));

    if (!invitationService.sendNewToken(email)) {
      response.put("message", messagesConfig.getMessages().get("emailSendError"));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    response.put("message", messagesConfig.getMessages().get("emailSent"));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/regenerate")
  public ResponseEntity<HashMap<String, Object>> regenerateInvitationEmail(
      @RequestBody InvitationDTO invitationDTO) {
    HashMap<String, Object> response = new HashMap<>();

    if (!invitationService.validateInvitation(invitationDTO.getEmail())) {
      response.put("message", messagesConfig.getMessages().get("tokenInvalid"));
      return ResponseEntity.badRequest().body(response);
    }

    if (!invitationService.resendToken(invitationDTO.getEmail())) {
      response.put("message", messagesConfig.getMessages().get("emailSendError"));
      return ResponseEntity.badRequest().body(response);
    }

    response.put("message", messagesConfig.getMessages().get("tokenValid"));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  public ResponseEntity<HashMap<String, Object>> validateInvitation(
      @RequestBody InvitationDTO invitationDTO) {
    HashMap<String, Object> response = new HashMap<>();

    if (!invitationService.validateInvitation(invitationDTO.getEmail(), invitationDTO.getToken())) {
      response.put("message", messagesConfig.getMessages().get("tokenInvalid"));
      return ResponseEntity.badRequest().body(response);
    }

    response.put("message", messagesConfig.getMessages().get("tokenValid"));
    return ResponseEntity.ok(response);
  }
}
