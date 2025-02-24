package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.servicios.CompanyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
public class ControllerCompany {

  private final CompanyService companyService;

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(this.companyService.findAll());
  }

  @PostMapping
  public ResponseEntity<Company> addNewCompany(@RequestBody Company company) {
    return ResponseEntity.ok(this.companyService.add(company));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}")
  public ResponseEntity<Company> getCompanyByUUID(@RequestParam UUID uuid) {
    return ResponseEntity.ok(companyService.getCompanyById(uuid));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{uuid}/clients/{email}")
  public ResponseEntity<User> addClient(
      @PathVariable(name = "uuid") UUID companyUuid,
      @PathVariable(name = "email") String emailClient) {

    return ResponseEntity.ok(this.companyService.addUserToCompany(emailClient, companyUuid));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/groups")
  public ResponseEntity<WrapperUserDTO> getEmployeeGroups(Pageable pageable) {
    return ResponseEntity.ok(new WrapperUserDTO(this.companyService.getGroupsCompany(pageable)));
  }
}
