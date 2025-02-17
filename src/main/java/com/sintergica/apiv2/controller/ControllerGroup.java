package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.servicios.GroupService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
public class ControllerGroup {

  @Autowired private GroupRepository groupRepository;
  private GroupService groupService;

  public ControllerGroup(GroupService groupService) {
    this.groupService = groupService;
  }

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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/addNewClientToGroup")
  public ResponseEntity<Map<String, String>> addNewClientToGroup(
      @RequestParam UUID groupId, @RequestParam String emailUser) {

    Map<String, String> serviceResponse = groupService.addUserToGroup(groupId, emailUser);

    if (serviceResponse.containsKey("success")) {
      return ResponseEntity.ok(serviceResponse);
    } else {
      return ResponseEntity.badRequest().body(serviceResponse);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/addNewGroup")
  public ResponseEntity<Map<String, String>> addNewGroup(
      @RequestParam String nameGroup,
      @RequestParam List<String> grantList,
      @RequestParam UUID companyUUID) {

    Map<String, String> response = groupService.addNewGroup(nameGroup, grantList, companyUUID);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getGroupByUUID")
  public ResponseEntity<Group> getGroupByUUID(@RequestParam UUID groupUUID) {
    return groupRepository
        .findById(groupUUID)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{uuid}/deleteUser/{email}")
  public ResponseEntity<Map<String, String>> deleteUser(
      @PathVariable(name = "uuid") UUID uuidGroup, @PathVariable("email") String emailClient) {

    Map<String, String> response = groupService.deleteUserFromGroup(uuidGroup, emailClient);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{uuid}/changeGrant/")
  public ResponseEntity<Map<String, String>> changeGrant(
      @PathVariable UUID uuid, @RequestParam List<String> items) {
    Map<String, String> response = this.groupService.changeGrants(uuid, items);
    return ResponseEntity.ok(response);
  }
}
