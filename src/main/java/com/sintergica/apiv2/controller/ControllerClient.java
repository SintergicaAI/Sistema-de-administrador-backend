package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ControllerClient {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody User user) {

    HashMap<String, String> response = new HashMap<>();
    boolean serviceResponse = userService.registerUser(user);

    if (!serviceResponse) {
      response.put("token", null);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    String token = this.userService.generateToken(user.getEmail());
    response.put("token", token);

    return ResponseEntity.badRequest().body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody User userRequest) {
    User user = userService.findByEmail(userRequest.getEmail());
    HashMap<String, String> response = new HashMap<>();

    if (user != null) {
      response.put("token", this.userService.generateToken(userRequest.getEmail()));
      return ResponseEntity.ok(response);
    }

    response.put("token", "null");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{email}/company/{uuid}")
  public ResponseEntity<String> addClientToCompany(
      @PathVariable(name = "email") String email, @PathVariable(name = "uuid") UUID targetCompany) {

    userService.addUserToCompany(email, targetCompany);

    return ResponseEntity.ok("Cliente agregado a compañia");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{email}/rol/{nameRol}")
  public ResponseEntity<String> changeRolClient(
      @PathVariable(name = "email") String email,
      @PathVariable(name = "nameRol") String newRolUser) {

      userService.changeUserRole(email, newRolUser);

      return ResponseEntity.ok("Rol cambiado");
  }

  @PostMapping("/{email}/addGroup/{uuidGroup}")
  public ResponseEntity<String> addGroup(
      @PathVariable String email, @PathVariable UUID uuidGroup) {

      userService.addUserToGroup(email, uuidGroup);
      return ResponseEntity.ok("Usuario agregado al grupo");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/getEmployeeGroups")
  public ResponseEntity<WrapperUserDTO> getEmployeeGroups(Pageable pageable) {
      Page<UserDTO> result = userService.getEmployeeGroupsRemastered(pageable);
      return ResponseEntity.ok(new WrapperUserDTO(result));
  }

}
