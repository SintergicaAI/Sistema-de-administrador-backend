package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.dto.RegisterResponseDTO;
import com.sintergica.apiv2.dto.RolRequestBodyDTO;
import com.sintergica.apiv2.dto.RolUserDTO;
import com.sintergica.apiv2.dto.TokenDTO;
import com.sintergica.apiv2.entidades.InvalidatedTokens;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.role.RolForbiddenException;
import com.sintergica.apiv2.exceptions.token.TokenForbidden;
import com.sintergica.apiv2.exceptions.user.EmailWrong;
import com.sintergica.apiv2.exceptions.user.PasswordConflict;
import com.sintergica.apiv2.exceptions.user.UserConflict;
import com.sintergica.apiv2.exceptions.user.UserForbidden;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.servicios.InvalidatedTokensService;
import com.sintergica.apiv2.servicios.InvitationService;
import com.sintergica.apiv2.servicios.RolService;
import com.sintergica.apiv2.servicios.UserService;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class ControllerClient {

  private final UserService userService;
  private final InvalidatedTokensService invalidatedTokensService;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final RolService rolService;
  private final InvitationService invitationService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDTO> register(
      @Valid @RequestBody User user,
      BindingResult result,
      @RequestParam(required = false) UUID signInToken) {

    User userFound = this.userService.findByEmail(user.getEmail());

    boolean invitationResponse = invitationService.validateInvitation(user.getEmail(), signInToken);

    if (result.hasErrors()) {
      throw new EmailWrong(result.getFieldError().getDefaultMessage());
    }

    if (!invitationResponse) {
      return ResponseEntity.status(HttpStatus.GONE).body(null);
    }

    if (userFound != null) {
      if (!userFound.isActive()) {
        throw new UserForbidden(
            "Este email ya esta registrado en el sistema solicita al administrador que te de alta nuevamente");
      }
      throw new UserConflict("Este email ya existe en el sistema");
    }

    user.setRol(rolRepository.findByName("USER"));
    user.setActive(true);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    User userCreated = this.userService.registerUser(user);

    this.invitationService.consumeInvitation(user.getEmail(), signInToken);

    RegisterResponseDTO responseDTO =
        RegisterResponseDTO.builder()
            .id(userCreated.getId())
            .email(userCreated.getEmail())
            .name(userCreated.getName())
            .lastName(userCreated.getLastName())
            .role(userCreated.getRol().getName())
            .token(userService.generateSessionToken(userCreated.getEmail()))
            .refreshToken(userService.generateRefreshToken(user.getEmail()))
            .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginAndRegisterDTO> login(@Valid @RequestBody User user) {

    User userValid = this.userService.findByEmail(user.getEmail());

    if (userValid == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    User userFound = this.userService.login(user);

    if (userFound == null) {
      throw new PasswordConflict("Usuario o contraseñas incorrectos");
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
            userService.generateRefreshToken(user.getEmail()),
            userFound.getRol()));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<LoginAndRegisterDTO> generateNewToken() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    String refreshToken = TokenUtils.extractToken(request.getHeader("Authorization"));

    boolean invalidatedTokens = this.invalidatedTokensService.isTokenBanned(refreshToken);

    if (invalidatedTokens) {
      throw new TokenForbidden(
          "El token ya ha sido invalidado imposible enviar un refresh token inicia sesion nuevamente para generar uno nuevo");
    }

    Claims hasClaims = TokenUtils.getTokenClaims(refreshToken);

    if (hasClaims == null) {
      throw new TokenForbidden("El token no es valido");
    }

    String type = TokenUtils.getTypeToken(refreshToken);

    if (TokenUtils.SESSION_TOKEN.equals(type)) {
      throw new TokenForbidden("El token no es un token valido");
    }

    String token =
        this.userService.generateSessionToken(
            Objects.requireNonNull(TokenUtils.getTokenClaims(refreshToken)).getSubject());
    User user =
        this.userService.findByEmail(
            Objects.requireNonNull(TokenUtils.getTokenClaims(token)).getSubject());

    return ResponseEntity.ok(
        new LoginAndRegisterDTO(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getLastName(),
            token,
            refreshToken,
            user.getRol()));
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

    if (!TokenUtils.REFRESH_TOKEN.equals(TokenUtils.getTypeToken(refreshToken))) {
      throw new TokenForbidden("Envia un refresh token valido");
    }

    if (invalidatedTokensService.isTokenBanned(refreshToken)) {
      throw new TokenForbidden("Token banned");
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

  @PreAuthorize("hasRole('SUPERADMIN') or hasRole('OWNER') or hasRole('ADMIN')")
  @PatchMapping("/{email}/rol")
  public ResponseEntity<RolUserDTO> changeRolClient(
      @PathVariable(name = "email") String email, @RequestBody RolRequestBodyDTO rol) {

    User userLogged = this.userService.getUserLogged();
    User userTarget = this.userService.findByEmail(email);

    if (userTarget == null) {
      throw new UserNotFound("Usuario not found");
    }

    Rol newRol = rolService.getRolByName(rol.name());
    Rol roleUserLogged = userLogged.getRol();
    Rol roleUserTarget = userTarget.getRol();

    if (newRol == null) {
      throw new RolForbiddenException("The role is not valid");
    }

    int weightRoleUserLogged = roleUserLogged.getWeight();
    int weightRoleUserTarget = roleUserTarget.getWeight();

    if (this.userService.canChangeRole(weightRoleUserLogged, weightRoleUserTarget)) {

      User user = this.userService.changeRol(userTarget, newRol);

      return ResponseEntity.ok(
          new RolUserDTO(user.getEmail(), user.getName(), user.getLastName(), user.getRol()));
    }

    throw new RolForbiddenException("You cannot modify a user with a higher role than yours.");
  }
}
