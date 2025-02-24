package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.servicios.GroupService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<Group>> getGroups() {
    return ResponseEntity.ok(groupService.findAll());
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
  public ResponseEntity<Group> addGroup(
      @PathVariable String email, @PathVariable(name = "uuid") UUID uuidGroup) {

    User user = this.groupService.getUserService().findByEmail(email);
    Optional.ofNullable(user)
        .orElseThrow(
            () -> {
              throw new UserNotFound("User not found");
            });

    Optional<Group> group = this.groupService.getGroupRepository().findById(uuidGroup);
    group.orElseThrow(() -> new GroupNotFound("Group not found"));

    if (!user.getCompany().getId().equals(group.get().getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    return ResponseEntity.ok(groupService.addUser(user, group.get()));
  }
}
