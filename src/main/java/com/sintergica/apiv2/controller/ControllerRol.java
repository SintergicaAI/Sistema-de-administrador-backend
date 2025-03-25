package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.RolUserDTO;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.role.RolForbiddenException;
import com.sintergica.apiv2.exceptions.role.RolNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.RolService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class ControllerRol {

  private final RolService rolService;
  private final UserService userService;

  @PreAuthorize("hasRole('SUPERADMIN')")
  @GetMapping
  public ResponseEntity<List<Rol>> getRoles() {
    return ResponseEntity.ok().body(rolService.getRoles());
  }

  @PostMapping
  public ResponseEntity<Rol> addNewRol(@RequestParam String roleName) {

    Rol newRol = new Rol();
    newRol.setName(roleName);

    return ResponseEntity.ok().body(rolService.save(newRol));
  }

  @PreAuthorize("hasRole('SUPERADMIN') or hasRole('OWNER') or hasRole('ADMIN')")
  @PatchMapping("/{name}/clients/{email}")
  public ResponseEntity<RolUserDTO> changeRolClient(
      @PathVariable(name = "email") String email, @PathVariable(name = "name") String newRolUser) {

    User userLog = this.userService.getUserLogged();

    User userFound = userService.findByEmail(email);

    if (userFound == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    Rol rol = this.rolService.getRolByName(newRolUser);
    if (rol == null) {
      throw new RolNotFound("Rol no encontrado");
    }

    if ("USER".equals(userFound.getRol()) && "ADMIN".equals(newRolUser)
        || "OWNER".equals(userFound.getRol())) {
      userFound.setGroups(null);
    }

    if ("OWNER".equals(userFound.getRol())) {

      if (userLog.getCompany() == null || userFound.getCompany() == null) {
        throw new CompanyNotFound("El usuario o el administrador no tienen compañia asociada");
      }

      if (!userLog.getCompany().equals(userFound.getCompany())) {
        throw new CompanyUserConflict("El usuario y el owner no tienen la misma compañia asociada");
      }
    }

    if ("ADMIN".equals(userFound.getRol())) {
      if (userFound.getCompany() == null || userFound.getCompany() == null) {
        throw new CompanyNotFound("El usuario o el administrador no tienen compañia asociada");
      }

      if ("SUPERADMIN".equals(userFound.getRol().getName())
          || "OWNER".equals(userFound.getRol().getName())) {
        throw new RolForbiddenException(
            "Sin privilegios para eliminar a un usuario con mayor jerarquia");
      }

      if (!userLog.getCompany().equals(userFound.getCompany())) {
        throw new CompanyUserConflict("El usuario y el admin no tienen la misma compañia asociada");
      }
    }

    User user = this.rolService.changeUserRole(userFound, rol);

    return ResponseEntity.ok(
        new RolUserDTO(user.getEmail(), user.getName(), user.getLastName(), user.getRol()));
  }
}
