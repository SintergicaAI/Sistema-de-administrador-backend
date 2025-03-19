package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.servicios.GroupService;
import com.sintergica.apiv2.servicios.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ControllerGroup {

  private final GroupService groupService;
  private final UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<GroupDTO>> getGroups() {
    List<Group> groups = groupService.findAll();
    List<GroupDTO> groupDTOList = new ArrayList<>();

    groups.forEach(group -> groupDTOList.add(new GroupDTO(group.getId(), group.getName())));

    return ResponseEntity.ok(groupDTOList);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping
  public ResponseEntity<Group> addGroup(@RequestBody Group group, @RequestParam(required = false) Set<UUID> users) {

    User userLogged = this.userService.getUserLogged();

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("El usuario que ha iniciado sesion no tiene compañia asociada");
    }

    group.setCompany(userLogged.getCompany());
    group.setUser(new HashSet<>());

    Set<User> usersFound = this.userService.findByInIdsAndActiveAndCompanyList(users,true, userLogged.getCompany());
    group.getUser().addAll(usersFound);

    return ResponseEntity.ok(groupService.save(group));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<Group> getGroupByUUID(@PathVariable(name = "uuid") UUID groupUUID) {
    Group groupFound = groupService.findGroupById(groupUUID);
    if (groupFound == null) {
      throw new GroupNotFound("Grupo no encontrado");
    }
    return ResponseEntity.ok(groupFound);
  }
}
