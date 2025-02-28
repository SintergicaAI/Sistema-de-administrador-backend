package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.CompanyDTO;
import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.CompanyService;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @DeleteMapping("/clients/{email}")
  public ResponseEntity<CompanyDTO> deactivateCompanyUser(@PathVariable String email) {

    User userLogged = this.userService.getUserLogged();
    User userFound = this.userService.findByEmail(email);

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("El administrador no tiene una compañia asociada");
    }

    if (userFound.getCompany() == null) {
      throw new CompanyNotFound("El usuario no tiene una compañia asociada");
    }

    if (userFound == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    if (userLogged.getCompany() != userFound.getCompany()) {
      throw new CompanyUserConflict(
          "Su compañia no coincide con la del usuario a elminar porque no es parte de su organización");
    }

    userFound = this.companyService.deleteUserFromCompany(userFound);

    return ResponseEntity.ok(
        new CompanyDTO(
            userLogged.getCompany().getId(),
            userLogged.getName(),
            userFound.getEmail(),
            userFound.getRol(),
            userFound.isActive()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/clients/{email}")
  public ResponseEntity<CompanyDTO> addClient(@PathVariable(name = "email") String emailClient) {

    String userNameAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
    User admin = this.userService.findByEmail(userNameAdmin);

    User userFound = userService.findByEmail(emailClient);

    if (userFound == null) {
      throw new UserNotFound("User not found");
    }

    if (userFound.getCompany() != null) {
      throw new CompanyUserConflict("El usuario ya tiene asociada una compañia");
    }

    if (admin.getCompany() == null) {
      throw new CompanyNotFound("El administrador no tiene una compañia asociada");
    }

    Optional<Company> company = this.companyService.findById(admin.getCompany().getId());

    if (company.isEmpty()) {
      throw new CompanyNotFound("Company not found");
    }

    User user = this.companyService.addUserToCompany(userFound, company.get());

    return ResponseEntity.ok(
        new CompanyDTO(
            company.get().getId(),
            company.get().getName(),
            user.getEmail(),
            user.getRol(),
            user.isActive()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/groups")
  public ResponseEntity<List<GroupDTO>> getGroupsCompany() {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userService.findByEmail(userName);

    if (user == null) {
      throw new UserNotFound("User not found");
    }

    if (user.getCompany() == null) {
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
  public ResponseEntity<WrapperUserDTO<UserDTO>> getEmployeeGroups(Pageable pageable) {

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
        new WrapperUserDTO<UserDTO>(this.companyService.getGroupsCompany(companyUser, pageable)));
  }
}
