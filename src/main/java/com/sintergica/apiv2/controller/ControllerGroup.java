package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.dto.GroupCreatedDTO;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
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
                    group.getName(),
                    group.getUser().stream().map(User::getId).collect(Collectors.toSet()),
                    group.getCreationDate(),
                    group.getEditDate(),
                    group.getUserCreator() == null ? null : group.getUserCreator().getName())));

    return ResponseEntity.ok(groupDTOList);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
  @PostMapping
  public ResponseEntity<GroupCreatedDTO> addGroup(@RequestBody GroupCreatedDTO groupDTO) {

    User userLogged = this.userService.getUserLogged();

    if (userLogged.getCompany() == null) {
      throw new CompanyNotFound("El usuario que ha iniciado sesion no tiene compañia asociada");
    }

    Group group =
        Group.builder()
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

    Group groupCreated = groupService.save(group);

    return ResponseEntity.ok(
        new GroupCreatedDTO(
            groupCreated.getName(),
            groupCreated.getUser().stream().map(User::getId).collect(Collectors.toSet()),
            groupCreated.getCreationDate(),
            groupCreated.getEditDate(),
            userLogged.getName()));
  }

  @GetMapping("/{name}")
  public ResponseEntity<GroupCreatedDTO> getGroupByUUID(@PathVariable(name = "name") String nameGroup) {
    Group groupFound = groupService.findGroupByCompanyAndName(this.userService.getUserLogged().getCompany(), nameGroup);
    if (groupFound == null) {
      throw new GroupNotFound("Grupo no encontrado");
    }

    return ResponseEntity.ok(
            new GroupCreatedDTO(
                    groupFound.getName(),
                    groupFound.getUser().stream().map(User::getId).collect(Collectors.toSet()),
                    groupFound.getCreationDate(),
                    groupFound.getEditDate(),
                    groupFound.getName()));
  }
}
