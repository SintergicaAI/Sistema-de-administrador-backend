package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.*;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.*;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;

import java.util.*;

import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<GroupDTO>> getGroups() {
    List<Group> groups = groupService.findAll();
    List<GroupDTO> groupDTOList = new ArrayList<>();

    groups.forEach(group ->
      groupDTOList.add(new GroupDTO(group.getId(), group.getName()))
    );

    return ResponseEntity.ok(groupDTOList);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Group> addGroup(@RequestBody Group group) {
    return ResponseEntity.ok(groupService.save(group));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<Group> getGroupByUUID(@PathVariable(name = "uuid") UUID groupUUID) {
    Group groupFound =
        Optional.ofNullable(groupService.findGroupById(groupUUID))
            .orElseThrow(() -> new GroupNotFound("Grupo no encontrado"));
    return ResponseEntity.ok(groupFound);
  }

  @PostMapping("{uuid}/clients/{email}")
  public ResponseEntity<GroupDTO> addGroup(
      @PathVariable String email, @PathVariable(name = "uuid") UUID uuidGroup) {

    User user = this.userService.findByEmail(email);
    Optional.ofNullable(user)
        .orElseThrow(
            () -> {
              throw new UserNotFound("User not found");
            });

    Optional<Group> group = this.groupService.findById(uuidGroup);
    group.orElseThrow(() -> new GroupNotFound("Group not found"));

    if(user.getCompany() == null){
      throw new CompanyNotFound("Usuario sin compañia asociada");
    }

    if (!user.getCompany().getId().equals(group.get().getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    Group groupTarget = groupService.addUser(user, group.get());
    return ResponseEntity.ok(new GroupDTO(groupTarget.getId(), groupTarget.getName()));
  }
}
