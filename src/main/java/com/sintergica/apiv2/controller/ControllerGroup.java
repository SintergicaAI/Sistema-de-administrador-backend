package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.*;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.group.GroupConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class ControllerGroup {

  private final GroupService groupService;
  private final UserService userService;

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @GetMapping
  public ResponseEntity<List<GroupCreatedDTO>> getGroups() {
    List<Group> groups = groupService.findAll();
    List<GroupCreatedDTO> groupDTOList = new ArrayList<>();

    groups.forEach(
        group ->
            groupDTOList.add(
                new GroupCreatedDTO(
                    group.getCompositeKey(),
                    group.getName(),
                    group.getUser().stream().map(User::getEmail).collect(Collectors.toSet()),
                    group.getCreationDate(),
                    group.getEditDate(),
                    group.getUserCreator() == null ? null : group.getUserCreator().getName())));

    return ResponseEntity.ok(groupDTOList);
  }

  @DeleteMapping("/{groupIDs}")
  public ResponseEntity<GroupDTO> deleteGroup(@PathVariable(name = "groupIDs") String name) {

    Group groupDelete =
        this.groupService.deleteGroup(name, this.userService.getUserLogged().getCompany());
    return ResponseEntity.ok(new GroupDTO(groupDelete.getCompositeKey(), groupDelete.getName()));
  }

/**
 * Handles the HTTP POST request to add a new group to the logged-in user's company.
 *
 * @param new_group the data transfer object containing the details of the group to be added.
 * @return a ResponseEntity containing a GroupCreatedDTO representing the newly created group.
 * @throws CompanyNotFound if the user does not have an associated company.
 * @throws GroupConflict if a group with the same name already exists in the company.
 * @see GroupService#addGroupWithUniqueKey(Group)
 *
 */
  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping //end
  public ResponseEntity<GroupCreatedDTO> addGroup(@RequestBody GroupCreatedDTO new_group) {

    User userLogged = this.userService.getUserLogged();

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("User has no company");
    }

    Group group =
        Group.builder()
            .compositeKey(new_group.groupKey())
            .name(new_group.name())
            .user(new HashSet<>())
            .company(userLogged.getCompany())
            .userCreator(userLogged)
            .creationDate(new Date())
            .editDate(new Date())
            .build();

    if (new_group.users() != null) {
      Collection<User> usersFound = this.userService.loginEmailsAndActiveUsers(new_group.users().stream().toList(), userLogged.getCompany());

      if (usersFound == null)
        throw new UserNotFound("There are an user dont exist or is not active");

      group.getUser().addAll(usersFound);
    }

    Group groupCreated;

    if (this.groupService.findGroupByCompanyAndName(
            this.userService.getUserLogged().getCompany(), group.getName())
        != null) {
      throw new GroupConflict("Groups's name already exists and could not be added");
    }

    if (this.groupService.existsByCompositeKey(new_group.groupKey())) {
      groupCreated = this.groupService.addGroupWithUniqueKey(group);
    } else {
      groupCreated = groupService.save(group);
    }

    return ResponseEntity.ok(
        new GroupCreatedDTO(
            groupCreated.getCompositeKey(),
            groupCreated.getName(),
            groupCreated.getUser().stream().map(User::getEmail).collect(Collectors.toSet()),
            groupCreated.getCreationDate(),
            groupCreated.getEditDate(),
            userLogged.getName()));
  }

  @GetMapping("/{compositeKey}") //end
  public ResponseEntity<ResponseGroupDTO> getGroupByUUID(
      @PathVariable(name = "compositeKey") String compositeKey) {

    Group groupFound =
        groupService.findByCompanyAndCompositeKey(
            this.userService.getUserLogged().getCompany(), compositeKey);
    if (groupFound == null) {
      throw new GroupNotFound("Grupo no encontrado");
    }

    return ResponseEntity.ok(
        new ResponseGroupDTO(
            groupFound.getCompositeKey(),
            groupFound.getName(),
            groupFound.getUser().stream().map(user -> new UserDataForGroupsDTO(user.getEmail(), user.getName(), user.getLastName())).collect(Collectors.toSet()),
            groupFound.getCreationDate(),
            groupFound.getEditDate(),
            groupFound.getUserCreator().getName()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PatchMapping("/{groupIDs}")
  public ResponseEntity<DataUserForGroupDTO> editGroup(@PathVariable (name = "groupIDs") String groupKey, @RequestBody DataUserForGroupDTO groupCreatedDTO){

    Group groupFound = this.groupService.findByCompanyAndCompositeKey(this.userService.getUserLogged().getCompany(), groupKey);

    if(groupFound == null){
      throw new GroupNotFound("Group not found");
    }

    if(groupCreatedDTO.name() != null)
      groupFound.setName(groupCreatedDTO.name());

    if(groupCreatedDTO.email() == null){
      this.groupService.save(groupFound);

      return ResponseEntity.ok(groupCreatedDTO);
    }

    groupFound.setUser(new HashSet<>());
    this.groupService.save(groupFound);

    for (String email : groupCreatedDTO.email()) {
      if(this.userService.findByEmail(email).getCompany() == this.userService.getUserLogged().getCompany()){
        groupFound.getUser().add(this.userService.findByEmail(email));
      }
    }

    this.groupService.save(groupFound);

    return ResponseEntity.ok(groupCreatedDTO);
  }

}
