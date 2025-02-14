package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.servicios.CompanyService;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class ControllerCompany {

  private final CompanyService companyService;

  public ControllerCompany(CompanyService companyService) {
    this.companyService = companyService;
  }

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(this.companyService.findAll());
  }

  @PostMapping("/add")
  public ResponseEntity<HashMap<String, Boolean>> addNewCompany(@RequestBody Company company) {
    HashMap<String, Boolean> response = companyService.add(company);

    if (response.containsKey("success")) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}")
  public ResponseEntity<HashMap<String, Company>> getCompanyByUUID(@RequestParam UUID uuid) {
    HashMap mapCompany = companyService.getCompanyByUUID(uuid);

    if (mapCompany.containsKey("success")) {
      return ResponseEntity.ok(mapCompany);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapCompany);
  }

  @PostMapping("{uuid}/deleteUser/{email}")
  public ResponseEntity<String> deleteUserToCompany(
      @PathVariable UUID uuid, @PathVariable String email) {

    HashMap<String, String> response = this.companyService.deleteUserToCompany(uuid, email);

    if (response.containsKey("error")) {
      return ResponseEntity.badRequest().body(response.get("error"));
    }

    return ResponseEntity.ok("success");
  }
}
