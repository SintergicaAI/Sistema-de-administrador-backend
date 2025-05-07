package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.*;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.role.RoleNotAllowedInGroupException;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.CompanyService;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;

import java.util.*;
import java.util.stream.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling company-related operations.
 *
 * @author Javier Palacios
 * 26/02/2025
 */

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class ControllerCompany {

  private final CompanyService companyService;
  private final UserService userService;
  private final GroupService groupService;
  private final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer;

  /**
   * Handles the HTTP GET request to retrieve all the companies in the system.
   *
   * @return a ResponseEntity containing a list of Company objects.
   */
  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(this.companyService.findAll());
  }



  /**
   * Handles the HTTP POST request to add a new company to the system.
   *
   * @param companyDTO the data transfer object containing the details of the company to be added.
   * @return a ResponseEntity containing a Company object representing the newly created company.
   * @throws CompanyUserConflict if the user trying to add the company does not have the sufficient
   *     permissions to do so.
   */
  @PreAuthorize("hasRole('SUPERADMIN') or hasRole('OWNER')")
  @PostMapping
  public ResponseEntity<Company> addNewCompany(@RequestBody CreateANewCompanyDTO companyDTO) {
    Company company = new Company();
    company.setName(companyDTO.name());
    company.setRFC(companyDTO.RFC());
    company.setName(companyDTO.name());
    company.setAddress(companyDTO.address());

    return ResponseEntity.ok(this.companyService.add(company));
  }

  /**
   * Handles the HTTP DELETE request to deactivate a user from a company.
   *
   * @param email the email of the user to deactivate.
   * @return a ResponseEntity containing a CompanyDTO with the data of the user
   *     deactivated.
   */
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

    if (!userLogged.getCompany().equals(userFound.getCompany())) {
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

  /**
   * Handles the HTTP POST request to add a user to the admin's company.
   *
   * @param emailClient the email of the user to be added to the company.
   * @return a ResponseEntity containing a CompanyDTO with the user's new company association.
   * @throws UserNotFound if the user with the specified email is not found.
   * @throws CompanyUserConflict if the user is already associated with a company.
   * @throws CompanyNotFound if the admin's associated company is not found.
   */
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

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PatchMapping("/groups/{group_id}/members")
  public ResponseEntity<GroupCreatedDTO> listMembersGroup(@PathVariable(name = "group_id") String compositeKey, @RequestBody WrapperListOfUsers email_users) {

    Group referenceGroup = this.groupService.findByCompanyAndCompositeKey(this.userService.getUserLogged().getCompany(), compositeKey);

    if (referenceGroup == null) {
      throw new GroupNotFound("Group not found");
    }
    System.out.println("*********");
    System.out.println(email_users.emailsMembers());
    referenceGroup.setUser(new HashSet<>());
    this.groupService.save(referenceGroup);

    Collection<User> listOfUsers = this.userService.loginEmailsAndActiveUsers(email_users.emailsMembers(), this.userService.getUserLogged().getCompany());

    if(listOfUsers == null){
      throw new UserNotFound("There arent users with this emails or they are not active");
    }

    referenceGroup.setUser(listOfUsers.stream().collect(Collectors.toSet()));
    this.groupService.save(referenceGroup);

    return ResponseEntity.ok(
            new GroupCreatedDTO(
                    referenceGroup.getCompositeKey(),
                    referenceGroup.getName(),
                    referenceGroup.getUser().stream().map(User::getEmail).collect(Collectors.toSet()),
                    referenceGroup.getCreationDate(),
                    referenceGroup.getEditDate(),
                    referenceGroup.getUserCreator() == null ? null : referenceGroup.getUserCreator().getName()
            ));
  }

  /**
   * Handles the HTTP GET request to retrieve the groups associated with the authenticated user's company.
   *
   * @return a ResponseEntity containing a list of GetGroupDTO objects representing the associated groups.
   * @throws UserNotFound if the authenticated user is not found.
   * @throws CompanyNotFound if the authenticated user does not have an associated company.
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @GetMapping("/groups")
  public ResponseEntity<List<GetGroupDTO>> getGroupsCompany() {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userService.findByEmail(userName);

    if (user == null) {
      throw new UserNotFound("User not found");
    }

    if (user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada agrega una compañia al usuario");
    }

    List<Group> groupsInCompany = this.groupService.findGroupByCompany(user.getCompany());
    List<GetGroupDTO> groupDTOs = new ArrayList<>();

    for (Group group : groupsInCompany) {
      Set<GetUserDTO> userInGroup = group.getUser().stream().map(user1 -> new GetUserDTO(user1.getEmail(), user1.getName(), user1.getLastName(), user1.getRol())).collect(Collectors.toSet());
      groupDTOs.add(new GetGroupDTO(group.getCompositeKey(), group.getName(), new GetUserDTO(group.getUserCreator().getEmail(), group.getUserCreator().getName(), group.getUserCreator().getLastName(), group.getUserCreator().getRol()), group.getCreationDate(), group.getEditDate(), userInGroup));
    }

    return ResponseEntity.ok(groupDTOs);
  }

  /**
   * Handles the HTTP GET request to search users by name.
   *
   * @param username the username to search for.
   * @param pageable the pagination information.
   * @return a ResponseEntity containing a list of SearchUserDTO objects representing
   *     the users found.
   * @throws UserNotFound if the authenticated user is not found.
   * @throws CompanyNotFound if the user does not have an associated company.
   */
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

  /**
   * Handles the HTTP DELETE request to remove a user from a specific group within the company.
   *
   * @param name the composite key of the group from which the user is to be removed.
   * @param email the email of the user to be removed from the group.
   * @return a ResponseEntity containing a GroupDTO representing the group without the removed user.
   * @throws CompanyNotFound if the authenticated user or the group does not have an associated company.
   * @throws GroupNotFound if the specified group is not found.
   * @throws UserNotFound if the specified user is not found.
   * @throws CompanyUserConflict if the user and the group do not belong to the same company.
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @DeleteMapping("/groups/{groupIDs}/member/{email}")
  public ResponseEntity<GroupDTO> deleteClientToGroup(
      @PathVariable(name = "groupIDs") String name, @PathVariable String email) {

    if (this.userService.getUserLogged().getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Group group =
        this.groupService.findByCompanyAndCompositeKey(
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
        new GroupDTO(groupWithoutTheUser.getCompositeKey(), groupWithoutTheUser.getName()));
  }

  /**
   * Handles the HTTP POST request to add a user to a group within the company.
   *
   * @param email the email of the user to be added to the group.
   * @param name the composite key of the group to which the user is to be added.
   * @return a ResponseEntity containing a GroupDTO representing the group with the added user.
   * @throws CompanyNotFound if the authenticated user or the group does not have an associated company.
   * @throws GroupNotFound if the specified group is not found.
   * @throws UserNotFound if the specified user is not found.
   * @throws CompanyUserConflict if the user and the group do not belong to the same company.
   * @throws RoleNotAllowedInGroupException if the user's role is not allowed in the group.
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping("/groups/{groupIDs}/member/{email}")
  public ResponseEntity<GroupDTO> addUserToGroup(
      @PathVariable String email, @PathVariable(name = "groupIDs") String name) {

    User user = this.userService.findByEmail(email);
    if (user == null) {
      throw new UserNotFound("User not found");
    }

    if (user.getCompany() == null) {
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    Group group = this.groupService.findByCompanyAndCompositeKey(user.getCompany(), name);

    if (group == null) {
      throw new GroupNotFound("Group not found");
    }

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    if ("ADMIN".equals(user.getRol().getName()) || "OWNER".equals(user.getRol().getName())) {
      throw new RoleNotAllowedInGroupException("Los roles ADMIN y OWNER no pueden estar en grupos");
    }

    Group groupTarget = groupService.addUser(user, group);

    return ResponseEntity.ok(new GroupDTO(groupTarget.getCompositeKey(), group.getName()));
  }

  /**
   * Handles the HTTP GET request to retrieve users within the authenticated user's company, with
   * optional filtering by name and groups.
   *
   * @param userName the full name of the user to search for.
   * @param groupName a list of group names to filter by.
   * @param size the page size.
   * @param page the page number.
   * @return a ResponseEntity containing a WrapperUserDTO object representing the users found.
   * @see CompanyService#getUsersByCompanyAndOptionalUsername
   * @see CompanyService#getUsersByCompanyAndUsernameAndGroupsName
   * @see CompanyService#getGroupsByCompanyAndOptionalUsername
   * @throws UserNotFound if the authenticated user is not found.
   * @throws CompanyNotFound if the user does not have an associated company.
   */
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

  /**
   * Handles the HTTP PATCH request to override a user's groups within the authenticated user's
   * company.
   *
   * @param email the email of the user to be modified.
   * @param group_ids a list of group composite keys to override.
   * @return a ResponseEntity containing a GroupOverrideDTO object representing the old group
   *     association.
   * @throws UserNotFound if the user with the specified email is not found.
   * @see GroupService#overrideGroupsToUser
   */
  @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
  @PatchMapping("/users/{email}/groups")
  public ResponseEntity<GroupOverrideDTO> userOverrideGroups(
      @PathVariable String email, @RequestBody GroupOverrideListDTO group_ids) {

    User userFound = this.userService.findByEmail(email);

    if (userFound == null) {
      throw new UserNotFound("User not found");
    }

    return ResponseEntity.ok(
        this.groupService.overrideGroupsToUser(
            this.userService.getUserLogged().getCompany(), group_ids.group_ids(), email));
  }

  /**
   * Handles the HTTP GET request to retrieve the groups associated with a user within the
   * authenticated user's company.
   *
   * @param email the email of the user to retrieve.
   * @return a ResponseEntity containing a GroupOverrideDTO object representing the associated
   *     groups.
   * @throws UserNotFound if the user with the specified email is not found.
   * @throws CompanyNotFound if the user does not have the same associated company as the
   *     authenticated user.
   */
  @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
  @GetMapping("/users/{email}/groups")
  public ResponseEntity<GroupOverrideDTO> getUserGroups(
      @PathVariable(name = "email") String email) {

    Company userLoggedCompany = this.userService.getUserLogged().getCompany();
    User userFound = this.userService.findByEmail(email);

    if (userFound == null) {
      throw new UserNotFound("User not found");
    }

    if (!userLoggedCompany.equals(userFound.getCompany())) {
      throw new CompanyNotFound("El usuario no tiene esta compañia asociada");
    }

    GroupOverrideDTO groupOverrideDTO =
        new GroupOverrideDTO(userFound.getEmail(), new ArrayList<>());

    for (Group group : userFound.getGroups()) {
      groupOverrideDTO.groups().add(new GroupDTO(group.getCompositeKey(), group.getName()));
    }

    return ResponseEntity.ok(groupOverrideDTO);
  }
}
