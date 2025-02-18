package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.servicios.GroupService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    Map<String, Boolean> response = this.groupService.save(group);
    if (response.containsKey("success")) {
      return ResponseEntity.ok(group);
    }

    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @GetMapping("/getGroupByUUID")
  public ResponseEntity<Group> getGroupByUUID(@RequestParam UUID groupUUID) {
    return ResponseEntity.ok(groupService.findGroupById(groupUUID));
  }
}
