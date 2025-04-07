package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.GroupCreatedDTO;
import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.group.GroupConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    group.getUser().stream().map(User::getId).collect(Collectors.toSet()),
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

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping
  public ResponseEntity<GroupCreatedDTO> addGroup(@RequestBody GroupCreatedDTO groupDTO) {

    User userLogged = this.userService.getUserLogged();

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("User has no company");
    }

    Group group =
        Group.builder()
            .compositeKey(groupDTO.groupKey())
            .name(groupDTO.name())
            .user(new HashSet<>())
            .company(userLogged.getCompany())
            .userCreator(userLogged)
            .creationDate(new Date())
            .editDate(new Date())
            .build();

    if (groupDTO.users() != null) {
      Set<User> usersFound =
          this.userService.findByInIdsAndActiveAndCompanyList(
              groupDTO.users(), true, userLogged.getCompany());
      group.getUser().addAll(usersFound);
    }

    Group groupCreated;

    if (this.groupService.findGroupByCompanyAndName(
            this.userService.getUserLogged().getCompany(), group.getName())
        != null) {
      throw new GroupConflict("Groups's name already exists and could not be added");
    }

    if (this.groupService.existsByCompositeKey(groupDTO.groupKey())) {
      groupCreated = this.groupService.addGroupWithUniqueKey(group);
    } else {
      groupCreated = groupService.save(group);
    }

    return ResponseEntity.ok(
        new GroupCreatedDTO(
            groupCreated.getCompositeKey(),
            groupCreated.getName(),
            groupCreated.getUser().stream().map(User::getId).collect(Collectors.toSet()),
            groupCreated.getCreationDate(),
            groupCreated.getEditDate(),
            userLogged.getName()));
  }

  @GetMapping("/{name}")
  public ResponseEntity<GroupCreatedDTO> getGroupByUUID(
      @PathVariable(name = "name") String nameGroup) {
    Group groupFound =
        groupService.findGroupByCompanyAndName(
            this.userService.getUserLogged().getCompany(), nameGroup);
    if (groupFound == null) {
      throw new GroupNotFound("Grupo no encontrado");
    }

    return ResponseEntity.ok(
        new GroupCreatedDTO(
            groupFound.getCompositeKey(),
            groupFound.getName(),
            groupFound.getUser().stream().map(User::getId).collect(Collectors.toSet()),
            groupFound.getCreationDate(),
            groupFound.getEditDate(),
            groupFound.getName()));
  }
}
