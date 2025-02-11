package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.repositorio.RolRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rol")
public class ControllerRol {

  @Autowired private RolRepository rolRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<Rol>> getRol() {
    return ResponseEntity.ok().body(rolRepository.findAll());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/addNewRol")
  public ResponseEntity<Rol> addNewRol(@RequestBody Rol rol) {
    return ResponseEntity.ok().body(rolRepository.save(rol));
  }
}
