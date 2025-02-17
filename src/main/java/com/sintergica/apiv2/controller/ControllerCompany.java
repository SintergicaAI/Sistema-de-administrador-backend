package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.servicios.CompanyService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
  public ResponseEntity<Map<String, Boolean>> addNewCompany(@RequestBody Company company) {
    Map<String, Boolean> response = companyService.add(company);

    if (response.containsKey("success")) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}")
  public ResponseEntity<Map<String, Company>> getCompanyByUUID(@RequestParam UUID uuid) {
    Map mapCompany = companyService.getCompanyByUUID(uuid);

    if (mapCompany.containsKey("success")) {
      return ResponseEntity.ok(mapCompany);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapCompany);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/getEmployeeGroupsByCompany")
  public ResponseEntity<WrapperUserDTO> getEmployeeGroups(Pageable pageable) {
    try {

      Page<UserDTO> result = this.companyService.getEmployeeGroupsRemastered(pageable);

      return ResponseEntity.ok(new WrapperUserDTO(result));

    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(null);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/userName/{name}")
  public ResponseEntity<WrapperUserDTO> search(@PathVariable String name, Pageable pageable) {

    Page<UserDTO> result = this.companyService.searchUser(name, pageable);

    return ResponseEntity.ok(new WrapperUserDTO(result));
  }



}
