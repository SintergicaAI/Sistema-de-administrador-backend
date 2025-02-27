package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.CompanyDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.CompanyService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final UserService userService;

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

    Company company = companyService.getCompanyById(uuid);

    if (company == null) {
      throw new CompanyNotFound("Compañia no encontrada");
    }

    return ResponseEntity.ok(company);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{uuid}/clients/{email}")
  public ResponseEntity<CompanyDTO> addClient(
      @PathVariable(name = "uuid") UUID companyUuid,
      @PathVariable(name = "email") String emailClient) {

    User userFound = userService.findByEmail(emailClient);

    if (userFound == null) {
      throw new UserNotFound("User not found");
    }

    if (userFound.getCompany() != null) {
      throw new CompanyUserConflict("El usuario ya tiene asociada una compañia");
    }

    Optional<Company> company = this.companyService.findById(companyUuid);

    if (!company.isPresent()) {
      throw new CompanyNotFound("Company not found");
    }

    User user = this.companyService.addUserToCompany(userFound, company.get());

    return ResponseEntity.ok(
        new CompanyDTO(
            company.get().getId(), company.get().getName(), user.getEmail(), user.getRol()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/groups")
  public ResponseEntity<WrapperUserDTO> getEmployeeGroups(Pageable pageable) {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userService.findByEmail(userName);

    if (user == null) {
      throw new UserNotFound("User not found");
    }

    Company companyUser = user.getCompany();
    if (companyUser == null) {
      throw new CompanyNotFound("El usuario no tiene una compañia asociada");
    }

    return ResponseEntity.ok(
        new WrapperUserDTO(this.companyService.getGroupsCompany(companyUser, pageable)));
  }
}
