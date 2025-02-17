package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.UserService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody User user) {

    Map<String, String> serviceResponse = userService.registerUser(user);

    if (Boolean.TRUE.equals(serviceResponse.get("Exito"))) {
      return new ResponseEntity<>(serviceResponse, HttpStatus.CREATED);
    }

    return ResponseEntity.badRequest().body(serviceResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody User userRequest) {
    boolean isValidUser = userService.loginUser(userRequest);
    HashMap<String, String> response = new HashMap<>();

    if (isValidUser) {
      String token = this.userService.generateToken(userRequest.getEmail());
      response.put("mensaje", "Bienvenido");
      response.put("exitoso", "true");
      response.put("token", token);

      return ResponseEntity.ok(response);
    }

    response.put("mensaje", "Credenciales incorrectas");
    response.put("exitoso", "false");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{email}/company/{uuid}")
  public ResponseEntity<Map<String, String>> addClientToCompany(
      @PathVariable(name = "email") String email, @PathVariable(name = "uuid") UUID targetCompany) {

    try {
      Map<String, String> serviceResponse = userService.addUserToCompany(email, targetCompany);

      return ResponseEntity.ok(serviceResponse);
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{email}/rol/{nameRol}")
  public ResponseEntity<Map<String, String>> changeRolClient(
      @PathVariable(name = "email") String email,
      @PathVariable(name = "nameRol") String newRolUser) {

    try {
      Map<String, String> response = userService.changeUserRole(email, newRolUser);
      return ResponseEntity.ok(response);
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
    }
  }

  @PostMapping("/{email}/addGroup/{uuidGroup}")
  public ResponseEntity<Map<String, String>> addGroup(
      @PathVariable String email, @PathVariable UUID uuidGroup) {

    try {
      userService.addUserToGroup(email, uuidGroup);
      return ResponseEntity.ok(Map.of("Success", "true"));

    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Success", "false"));
    }
  }


}
