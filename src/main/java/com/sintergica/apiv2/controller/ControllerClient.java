package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ControllerClient {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<LoginAndRegisterDTO> register(@Valid @RequestBody User user) {
    return ResponseEntity.ok(this.userService.registerUser(user));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginAndRegisterDTO> login(@Valid @RequestBody User user) {
    return ResponseEntity.ok(this.userService.login(user));
  }
}
