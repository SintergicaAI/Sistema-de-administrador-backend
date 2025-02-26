package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.dto.SearchUserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.user.UserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
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

  @GetMapping("/login")
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

  @GetMapping("{username}/search")
  public ResponseEntity<WrapperUserDTO<SearchUserDTO>> searchUsers(
      @PathVariable String username, Pageable pageable) {

    User user = this.userService.getUserLogged();
    if (user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Page<SearchUserDTO> userPages =
        this.userService.getUsersByName(username, user.getCompany(), pageable);

    return ResponseEntity.ok(new WrapperUserDTO<>(userPages));
  }
}
