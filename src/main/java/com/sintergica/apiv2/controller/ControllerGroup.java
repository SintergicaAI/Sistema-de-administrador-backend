package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.servicios.GroupService;
import java.util.List;
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
    return ResponseEntity.ok(group);
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<Group> getGroupByUUID(@PathVariable(name = "uuid") UUID groupUUID) {
    return ResponseEntity.ok(groupService.findGroupById(groupUUID));
  }

  @PostMapping("{uuid}/clients/{email}")
  public ResponseEntity<Group> addGroup(
      @PathVariable String email, @PathVariable(name = "uuid") UUID uuidGroup) {
    return ResponseEntity.ok(groupService.addUser(email, uuidGroup));
  }
}
