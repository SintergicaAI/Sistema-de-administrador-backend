package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.servicios.CompanyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class ControllerCompany {

  private final CompanyService companyService;

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(this.companyService.findAll());
  }

  @PostMapping("/add")
  public ResponseEntity<String> addNewCompany(@RequestBody Company company) {
    this.companyService.add(company);
    return ResponseEntity.ok("Compañia agregada");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}")
  public ResponseEntity<Company> getCompanyByUUID(@RequestParam UUID uuid) {
    return ResponseEntity.ok(companyService.getCompanyById(uuid));
  }
}
