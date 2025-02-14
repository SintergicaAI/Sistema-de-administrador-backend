package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.InvitationService;
import com.sintergica.apiv2.servicios.UserService;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ControllerClient {

  private final UserService userService;
  private final InvitationService invitationService;

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user, @RequestParam UUID signInToken) {

    Pair<Boolean,String> invitationResponse = invitationService.validateInvitation(user.getEmail(), signInToken);

    if(!invitationResponse.a){
      return ResponseEntity.status(HttpStatus.GONE).body(null);
    }

    Map<String, Object> serviceResponse = userService.registerUser(user);

    serviceResponse.put("message",invitationResponse.b);
    if (Boolean.TRUE.equals(serviceResponse.get("Exito"))) {
      return new ResponseEntity<>(serviceResponse, HttpStatus.CREATED);
    }

    return ResponseEntity.badRequest().body(serviceResponse);

  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody User userRequest) {
    Map<String, Object> serviceResponse = userService.loginUser(userRequest);

    if (Boolean.TRUE.equals(serviceResponse.get("exitoso"))) {
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
  public ResponseEntity<Map<String, Object>> addGroup(
      @PathVariable String email, @PathVariable UUID uuidGroup) {

    try {
      userService.addUserToGroup(email, uuidGroup);
      return ResponseEntity.ok(Map.of("Success", true));

    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("Success", false, "message", ex.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/getEmployeeGroupsForCompany")
  public ResponseEntity<Page<Map<String, Object>>> getEmployeeGroupsForCompany(Pageable pageable) {
    Page<Map<String, Object>> result = userService.getEmployeeGroups(pageable);
    return ResponseEntity.ok(result);
  }
}
