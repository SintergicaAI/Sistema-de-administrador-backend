package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.RolUserDTO;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.role.RolNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.RolService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class ControllerRol {

  private final RolService rolService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<Rol>> getRoles() {
    return ResponseEntity.ok().body(rolService.getRoles());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Rol> addNewRol(@RequestBody Rol rol) {
    return ResponseEntity.ok().body(rolService.save(rol));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("{name}/clients/{email}")
  public ResponseEntity<RolUserDTO> changeRolClient(
      @PathVariable(name = "email") String email, @PathVariable(name = "name") String newRolUser) {

    User userFound =
        Optional.ofNullable(rolService.getUserService().findByEmail(email))
            .orElseThrow(
                () -> {
                  throw new UserNotFound("Usuario no encontrado");
                });

    Rol rol = this.rolService.getRolByName(newRolUser);
    if (rol == null) {
      throw new RolNotFound("Rol no encontrado");
    }

    User user = this.rolService.changeUserRole(userFound, rol);
    return ResponseEntity.ok(
        new RolUserDTO(user.getEmail(), user.getName(), user.getLastName(), user.getRol()));
  }
}
