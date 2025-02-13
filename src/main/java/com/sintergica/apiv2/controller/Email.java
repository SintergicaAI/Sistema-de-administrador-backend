package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.configuration.EmailConfig;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sintergica.apiv2.utilidades.EmailUtils;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class Email {

    @Autowired private InvitationRepository invitationRepository;

    private final EmailConfig config;

    @PostMapping("/send")
    public ResponseEntity<HashMap<String, Object>> sendValidationEmail(@RequestBody EmailUtils.Email emailObject) {
        String URL= config.getBase_url();
        HashMap<String, Object> response = new HashMap<>();

        emailObject.appendToBody(URL + "?signInToken=" + emailObject.generateToken());
        System.out.println(emailObject);
        boolean isSuccess = EmailUtils.sendEmail(emailObject,config);

        response.put("to",emailObject.getRecipients());
        response.put("token",emailObject.getToken());

        Invitation invitation = new Invitation();
        invitation.setEmail(emailObject.getRecipients().split(",")[0]);
        invitation.setActive(true);
        invitation.setToken(emailObject.getToken());
        invitation.setExpireDate(LocalDateTime.now().plusDays(7));
        invitationRepository.save(invitation);

        System.out.println(
                invitationRepository.findById(emailObject.getToken())
        );

        if(!isSuccess){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @GetMapping("/hello")
    public String email() {
        return "Hola";
    }
}