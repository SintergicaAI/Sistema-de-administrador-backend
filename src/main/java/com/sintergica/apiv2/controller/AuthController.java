package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.AuthUserDTO;
import com.sintergica.apiv2.entidades.AuthEntity;
import com.sintergica.apiv2.entidades.UserEntity;
import com.sintergica.apiv2.repositorio.AuthRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class AuthController {

  private final UserRepository userRepository;
  private final AuthRepository authRepository;
  private final PasswordEncoder passwordEncoder;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<AuthEntity>> getClients() {
    return ResponseEntity.ok(this.authRepository.findAll());
  }

  @PostMapping("/register")
  public ResponseEntity<HashMap<String, Object>> register(@Valid @RequestBody AuthUserDTO authUserDTO) {

    HashMap<String, Object> map = new HashMap<>();

    if (this.authRepository.findByEmail(authUserDTO.getEmail()) == null) {

      UserEntity user = new UserEntity();

      user.setName(authUserDTO.getUsername());
      user.setEmail(authUserDTO.getEmail());
      user.setRole("user");
      user.setProfileImageUrl("/user.png");
      user.setCreatedAt(new Date().getTime());
      user.setUpdatedAt(new Date().getTime());

      AuthEntity authEntity = new AuthEntity();
      authEntity.setEmail(authUserDTO.getEmail());
      authEntity.setPassword(passwordEncoder.encode(authUserDTO.getPassword()));

      this.authRepository.save(authEntity);

      String token = generateToken(user.getEmail());

      map.put("Exito", true);
      map.put("token", token);

      return new ResponseEntity<>(map, HttpStatus.CREATED);

    }

    map.put("Exito", false);
    map.put("token", null);

    return ResponseEntity.badRequest().body(map);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthUserDTO authEntity) {

    Map<String, Object> respuesta = new HashMap<>();
    AuthEntity user = this.authRepository.findByEmail(authEntity.getEmail());

    if (user != null && passwordEncoder.matches(authEntity.getPassword(), user.getPassword())) {
      String token = this.generateToken(authEntity.getEmail());

      respuesta.put("mensaje", "Bienvenidos");
      respuesta.put("exitoso", true);
      respuesta.put("token", token);

      return new ResponseEntity<>(respuesta, HttpStatus.OK);

    }

    respuesta.put("mensaje", "credenciales incorrectas");
    respuesta.put("exitoso", false);

    return ResponseEntity.badRequest().body(respuesta);
  }


  private String generateToken(String email) {
    return TokenUtilidades.createToken(Jwts.claims().subject(email).build());
  }
}
