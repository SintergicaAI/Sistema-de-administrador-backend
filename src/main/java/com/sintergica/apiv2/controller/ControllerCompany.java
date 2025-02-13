package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/company")
public class ControllerCompany {

  @Autowired private CompanyRepository companyRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;

  @GetMapping
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(companyRepository.findAll());
  }

  @PostMapping("/add")
  public ResponseEntity<Company> addNewCompany(@RequestBody Company company) {
    return ResponseEntity.ok(companyRepository.save(company));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}")
  public ResponseEntity<Company> getCompanyByUUID(@RequestParam UUID uuid) {
    return companyRepository
        .findById(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{uuid}/get")
  public ResponseEntity<Company> getCompany(@PathVariable UUID uuid) {

    Optional<Company> company = companyRepository.findById(uuid);

    if (company.isPresent()) {
      List<User> usersCompany = userRepository.findByCompany(company.get());
    }

    return null;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{uuid}/client/{id}")
  public ResponseEntity<Map<Group, List<User>>> getClientGroupsUser(
      @PathVariable UUID uuid, @PathVariable(name = "id") UUID idClient) {

    return companyRepository
        .findById(uuid)
        .map(
            company ->
                groupRepository.findAllByCompany(company).stream()
                    .map(
                        group ->
                            Map.entry(
                                group,
                                group.getUser().stream()
                                    .filter(user -> user.getId().equals(idClient))
                                    .collect(Collectors.toList())))
                    .filter(entry -> !entry.getValue().isEmpty())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.ok(Collections.emptyMap()));
  }
}
