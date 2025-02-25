package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.*;
import com.sintergica.apiv2.entidades.*;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.*;

import java.util.*;

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
  private final GroupService groupService;

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
  public ResponseEntity<List<GroupDTO>> getGroupsCompany() {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userService.findByEmail(userName);

    if(user == null) {
      throw new UserNotFound("User not found");
    }

    if(user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada agrega una compañia al usuario");
    }

    List<Group> groupsInCompany = this.groupService.findGroupByCompany(user.getCompany());
    List<GroupDTO> groupDTOs = new ArrayList<>();

    for (Group group : groupsInCompany) {
      groupDTOs.add(new GroupDTO(group.getId(), group.getName()));
    }

    return ResponseEntity.ok(groupDTOs);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clients")
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
