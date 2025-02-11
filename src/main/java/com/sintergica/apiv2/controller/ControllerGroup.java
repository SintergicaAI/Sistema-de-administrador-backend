package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Grant;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GrantRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ControllerGroup {

  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private GrantRepository grantRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<Group>> getGroups() {
    return ResponseEntity.ok(groupRepository.findAll());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Group> addGroup(@RequestBody Group group) {
    return ResponseEntity.ok(groupRepository.save(group));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/addNewClientToGroup")
  public ResponseEntity<HashMap<String, String>> addNewClientToGroup(
      @RequestParam UUID groupId, @RequestParam String emailUser) {

    Optional<Group> currentGroup = groupRepository.findById(groupId);
    User currentUser = userRepository.findByEmail(emailUser);

    if (currentGroup != null
        && currentUser != null
        && currentUser.getCompany() != null
        && currentGroup.get().getCompany() != null
        && (currentUser.getCompany().getId() == currentGroup.get().getCompany().getId())) {

      currentGroup.get().getUser().add(currentUser);
      groupRepository.save(currentGroup.get());

      return ResponseEntity.ok(
          new HashMap<>() {
            {
              put("Exito", "Usuario agregado al grupo");
            }
          });
    }

    return ResponseEntity.badRequest()
        .body(
            new HashMap<>() {
              {
                put(
                    "Sin exito",
                    "Uno de los campos es nulo o la empresa no esta asociada con el usuario y grupo");
              }
            });
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/addNewGroup")
  public ResponseEntity<HashMap<String, Object>> addNewGroup(
      @RequestParam String nameGroup,
      @RequestParam List<String> grantList,
      @RequestParam UUID companyUUID) {

    Optional<Company> company = companyRepository.findById(companyUUID);

    if (company.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              new HashMap<>() {
                {
                  put("Sin exito", "La compañia no existe");
                }
              });
    }

    Set<Grant> grants =
        grantList.stream()
            .map(grantRepository::findByName)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Group newGroup = new Group();
    newGroup.setName(nameGroup);
    newGroup.setCompany(company.get());
    newGroup.setGrant(grants);
    groupRepository.save(newGroup);

    return ResponseEntity.ok(
        new HashMap<>() {
          {
            put("Exito", "Grupo agregado");
          }
        });
  }

  @GetMapping("/getGroupByUUID")
  public ResponseEntity<Group> getGroupByUUID(@RequestParam UUID groupUUID) {
    return groupRepository
        .findById(groupUUID)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
