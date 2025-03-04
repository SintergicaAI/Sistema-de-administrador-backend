package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.dto.TokenDTO;
import com.sintergica.apiv2.entidades.InvalidatedTokens;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.token.TokenForbidden;
import com.sintergica.apiv2.exceptions.user.UserConflict;
import com.sintergica.apiv2.exceptions.user.UserForbidden;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.InvalidatedTokensService;
import com.sintergica.apiv2.servicios.UserService;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class ControllerClient {

  private final UserService userService;
  private final InvalidatedTokensService invalidatedTokensService;

  @PostMapping("/register")
  public ResponseEntity<LoginAndRegisterDTO> register(@Valid @RequestBody User user) {
    User userFound = this.userService.findByEmail(user.getEmail());

    if (userFound != null) {
      throw new UserConflict("Este email ya existe en el sistema");
    }

    if (userFound != null && !userFound.isActive()) {
      throw new UserForbidden(
          "Este email ya esta registrado en el sistema solicita al administrador que te de alta nuevamente");
    }

    User userCreated = this.userService.registerUser(user);

    return ResponseEntity.ok(
        new LoginAndRegisterDTO(
            userCreated.getId(),
            userCreated.getEmail(),
            userCreated.getName(),
            userCreated.getLastName(),
            userService.generateSessionToken(user.getEmail()),
            userService.generateRefreshToken(user.getEmail())));
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

    if (!userFound.isActive()) {
      throw new UserForbidden("Solicita nuevamente acceso a la empresa");
    }

    return ResponseEntity.ok(
        new LoginAndRegisterDTO(
            userFound.getId(),
            userFound.getEmail(),
            userFound.getName(),
            userFound.getLastName(),
            userService.generateSessionToken(userFound.getEmail()),
            userService.generateRefreshToken(user.getEmail())));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<String> generateNewToken() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    String refreshToken = TokenUtils.extractToken(request.getHeader("Authorization"));

    InvalidatedTokens invalidatedTokens =
        this.invalidatedTokensService.getInvalidatedToken(refreshToken);

    if (invalidatedTokens != null) {
      throw new TokenForbidden(
          "El token ya ha sido invalidado imposible enviar un refresh token inicia sesion nuevamente para generar uno nuevo");
    }

    return ResponseEntity.ok(
        this.userService.generateSessionToken(
            TokenUtils.getTokenClaims(refreshToken).getSubject()));
  }

  @GetMapping("/updateTokens")
  public ResponseEntity<?> updateTokens() {
    invalidatedTokensService.loadBannedTokens();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<TokenDTO> logout() {

    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String refreshToken = TokenUtils.extractToken(request.getHeader("Authorization"));

    if (refreshToken == null) {
      throw new UserNotFound("Usuario sin refresh token");
    }

    if (!TokenUtils.getTypeToken(refreshToken).equals(TokenUtils.REFRESH_TOKEN)) {
      throw new TokenForbidden("Envia un refresh token valido");
    }

    Claims refreshClaims = TokenUtils.getTokenClaims(refreshToken);

    InvalidatedTokens invalidatedTokens =
        invalidatedTokensService.addInvalidatedToken(
            new InvalidatedTokens(
                refreshToken,
                this.userService.findByEmail(refreshClaims.getSubject()),
                new Date(),
                refreshClaims.getExpiration()));

    return ResponseEntity.ok(
        new TokenDTO(invalidatedTokens.getRefreshToken(), refreshClaims.getSubject()));
  }
}
