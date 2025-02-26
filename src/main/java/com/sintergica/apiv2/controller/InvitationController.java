package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.configuration.MessagesConfig;
import com.sintergica.apiv2.servicios.EmailService;
import com.sintergica.apiv2.servicios.InvitationService;
import com.sintergica.apiv2.utilidades.EmailUtils;
import java.util.HashMap;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
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
  private final EmailService emailService;
  private final MessagesConfig messagesConfig;
  private final InvitationService invitationService;

  @GetMapping
  public String hello() {
    return messagesConfig.getMessages().get("testMesssage");
  }

  @PostMapping("/send")
  public ResponseEntity<HashMap<String, Object>> sendInvitationEmail(
      @RequestBody EmailUtils.Email emailObject) {
    HashMap<String, Object> response = new HashMap<>();
    Pair<Boolean, String> emailResponse = emailService.sendToken(emailObject);

    response.put("message", emailResponse.b);

    if (!emailResponse.a) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  public ResponseEntity<HashMap<String,Object>> validateInvitation(@RequestBody String email, @RequestBody UUID invitationToken){
    HashMap<String,Object> response = new HashMap<>();
    if(!invitationService.validateInvitation(email,invitationToken)){
      response.put("message",messagesConfig.getMessages().get("tokenInvalid"));
      return ResponseEntity.badRequest().body(response);
    }

    response.put("message",messagesConfig.getMessages().get("tokenValid"));
    return ResponseEntity.ok(response);
  }
}
