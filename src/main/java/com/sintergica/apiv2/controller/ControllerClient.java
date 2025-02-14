package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.UserService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    Map<String, String> serviceResponse = userService.loginUser(userRequest);

    if (serviceResponse.get("exitoso").equals("true")) {
      return ResponseEntity.ok(serviceResponse);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(serviceResponse);
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/getEmployeeGroupsForCompanyRemastered")
  public ResponseEntity<WrapperUserDTO> getEmployeeGroupsForCompanyRemastered(Pageable pageable) {
    try {

      Page<UserDTO> result = userService.getEmployeeGroupsRemastered(pageable);

      return ResponseEntity.ok(new WrapperUserDTO(result));

    } catch (ResponseStatusException ex) {
      Page<UserDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      return ResponseEntity.status(ex.getStatusCode()).body(null);
    }
  }
}
