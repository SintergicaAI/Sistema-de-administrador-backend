package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.CompanyDTO;
import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.SearchUserDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.dto.WrapperUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.role.RoleNotAllowedInGroupException;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.CompanyService;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
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
  private final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer;

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(this.companyService.findAll());
  }

  @PostMapping
  public ResponseEntity<Company> addNewCompany(@RequestBody Company company) {
    return ResponseEntity.ok(this.companyService.add(company));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @DeleteMapping("/users/{email}")
  public ResponseEntity<CompanyDTO> deactivateCompanyUser(@PathVariable String email) {

    User userLogged = this.userService.getUserLogged();
    User userFound = this.userService.findByEmail(email);

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("El administrador no tiene una compañia asociada");
    }

    if (userFound.getCompany() == null) {
      throw new CompanyNotFound("El usuario no tiene una compañia asociada");
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

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping("/users/{email}")
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

  @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
  @PostMapping("/groups/{name}")
  public ResponseEntity<GroupDTO> addGroup(@PathVariable(name = "name") String newGroupName) {

    User userLogged = this.userService.getUserLogged();

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("The user doesn't have a company");
    }

    Company companyUserLogged = userLogged.getCompany();

    Group group = new Group();
    group.setName(newGroupName);
    group.setCompany(companyUserLogged);

    Group newGroup = this.groupService.save(group);

    if (newGroup == null) {
      throw new GroupConflict("El usuario ya tiene un grupo");
    }

    return ResponseEntity.ok(new GroupDTO(newGroup.getId(), newGroup.getName()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
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

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @GetMapping("/users/{username}")
  public ResponseEntity<WrapperUserDTO<SearchUserDTO>> searchUsers(
      @PathVariable String username, Pageable pageable) {

    User user = this.userService.getUserLogged();
    if (user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Page<SearchUserDTO> userPages =
        this.userService.getUsersByName(username, user.getCompany(), pageable);

    return ResponseEntity.ok(new WrapperUserDTO<>(userPages));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @DeleteMapping("/groups/{name}/clients/{email}")
  public ResponseEntity<GroupDTO> deleteClientToGroup(
      @PathVariable(name = "name") String name, @PathVariable String email) {

    if (this.userService.getUserLogged().getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Group group =
        this.groupService.findGroupByCompanyAndName(
            this.userService.getUserLogged().getCompany(), name);
    User user = this.userService.findByEmail(email);

    if (group == null) {
      throw new GroupNotFound("Grupo no encontrado");
    }

    if (user == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    if (group.getCompany() == null) {
      throw new CompanyNotFound("Grupo sin compañia asociada");
    }

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    Group groupWithoutTheUser = this.groupService.deleteUser(group, user);

    return ResponseEntity.ok(
        new GroupDTO(groupWithoutTheUser.getId(), groupWithoutTheUser.getName()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping("/groups/{name}/clients/{email}")
  public ResponseEntity<GroupDTO> addGroup(
      @PathVariable String email, @PathVariable(name = "name") String name) {

    User user = this.userService.findByEmail(email);
    if (user == null) {
      throw new UserNotFound("User not found");
    }

    if (user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Group group = this.groupService.findGroupByCompanyAndName(user.getCompany(), name);

    if (group == null) {
      throw new GroupNotFound("Group not found");
    }

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    if (user.getRol().getName().equals("ADMIN") || user.getRol().getName().equals("OWNER")) {
      throw new RoleNotAllowedInGroupException("Los roles ADMIN y OWNER no pueden estar en grupos");
    }

    Group groupTarget = groupService.addUser(user, group);

    return ResponseEntity.ok(new GroupDTO(groupTarget.getId(), groupTarget.getName()));
  }

  @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
  @GetMapping("/users")
  public ResponseEntity<WrapperUserDTO<UserDTO>> getUsersAndGroups(
      @RequestParam(required = false, name = "fullname") String userName,
      @RequestParam(required = false, name = "groups") List<String> groupName,
      @RequestParam(required = false, defaultValue = "-1") int size,
      @RequestParam(required = false, defaultValue = "-1") int page) {

    if (groupName == null) {
      return ResponseEntity.ok(
          new WrapperUserDTO<>(
              this.companyService.getUsersByCompanyAndOptionalUsername(
                  this.userService.getUserLogged().getCompany(), userName, null, size, page)));
    } else if (userName != null) {
      return ResponseEntity.ok(
          new WrapperUserDTO<>(
              this.companyService.getUsersByCompanyAndUsernameAndGroupsName(
                  userName, groupName, size, page)));
    }

    return ResponseEntity.ok(
        new WrapperUserDTO<>(
            this.companyService.getGroupsByCompanyAndOptionalUsername(groupName, size, page)));
  }

  @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
  @GetMapping("/users/{email}/groups")
  public ResponseEntity<List<GroupDTO>> getUserGroups(
      @PathVariable(name = "email") String email, @RequestParam String group) {

    Company userLoggedCompany = this.userService.getUserLogged().getCompany();
    User userFound = this.userService.findByEmail(email);

    if (userLoggedCompany != userFound.getCompany()) {
      throw new CompanyNotFound("El usuario no tiene esta compañia asociada");
    }

    List<Group> groupList =
        this.groupService.findByCompanyAndUserEmailAndGroupNameStartingWith(
            userLoggedCompany, email, group);

    List<GroupDTO> groupDTOList = new ArrayList<>();

    for (Group groupTemp : groupList) {
      groupDTOList.add(new GroupDTO(groupTemp.getId(), groupTemp.getName()));
    }

    return ResponseEntity.ok(groupDTOList);
  }
}
