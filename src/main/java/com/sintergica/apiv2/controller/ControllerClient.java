package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.user.UserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
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

    if (this.userService.findByEmail(user.getEmail()) != null) {
      throw new UserConflict("Este email ya existe en el sistema");
    }

    User userCreated = this.userService.registerUser(user);

    return ResponseEntity.ok(
        new LoginAndRegisterDTO(
            userCreated.getEmail(),
            userCreated.getName(),
            userCreated.getLastName(),
            userService.generateToken(user.getEmail())));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginAndRegisterDTO> login(@Valid @RequestBody User user) {

    User userValid = this.userService.findByEmail(user.getEmail());

    if (userValid == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    User userFound = this.userService.login(user);

    if (userFound == null) {
      throw new UserNotFound("Usuario o contraseñas incorrectos");
    }

    return ResponseEntity.ok(
        new LoginAndRegisterDTO(
            userFound.getEmail(),
            userFound.getName(),
            userFound.getLastName(),
            userService.generateToken(userFound.getEmail())));
  }
}
