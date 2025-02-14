package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.servicios.EmailService;
import com.sintergica.apiv2.utilidades.EmailUtils;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author user
 */
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class Email {
  private final EmailService emailService;

  @PostMapping("/sendtoken")
  public ResponseEntity<HashMap<String, Object>> sendValidationEmail(
      @RequestBody EmailUtils.Email emailObject) {
    HashMap<String, Object> response = new HashMap<>();
    Pair<Boolean, String> emailResponse = emailService.sendToken(emailObject);

    response.put("message", emailResponse.b);

    if (!emailResponse.a) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    return ResponseEntity.ok(response);
  }
}
