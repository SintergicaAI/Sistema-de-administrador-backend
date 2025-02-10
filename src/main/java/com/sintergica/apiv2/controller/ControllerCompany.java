package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class ControllerCompany {

  @Autowired private CompanyRepository companyRepository;

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanys() {
    return ResponseEntity.ok(companyRepository.findAll());
  }

  @PostMapping("/addNewCompany")
  public ResponseEntity<Company> addNewCompany(@RequestBody Company company) {
    return ResponseEntity.ok(companyRepository.save(company));
  }

  @PostMapping("/getCompany")
  public ResponseEntity<Company> getCompanyByUUID(@RequestParam UUID uuid) {
    return companyRepository
        .findById(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
