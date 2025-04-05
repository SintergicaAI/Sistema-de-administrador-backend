package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.RolService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final GroupService groupService;

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
}
