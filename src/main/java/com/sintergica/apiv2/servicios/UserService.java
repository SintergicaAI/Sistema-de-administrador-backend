package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final GroupRepository groupRepository;
  private final PasswordEncoder passwordEncoder;
  private final RolRepository rolRepository;
  private final RolService rolService;

  public Map<String, Object> registerUser(User user) {
    HashMap<String, Object> response = new HashMap<>();

    user.setRol(rolService.getRolByName("GUEST"));
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (userRepository.findByEmail(user.getEmail()) != null) {
      response.put("Exito", false);
      response.put("token", null);
      return response;
    }

    userRepository.save(user);
    String token = generateToken(user.getEmail());
    response.put("Exito", true);
    response.put("token", token);

    return response;
  }

  public Map<String, Object> loginUser(User userRequest) {
    Map<String, Object> response = new HashMap<>();

    User user = userRepository.findByEmail(userRequest.getEmail());

    if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
      String token = generateToken(user.getEmail());
      response.put("mensaje", "Bienvenido");
      response.put("exitoso", true);
      response.put("token", token);
    } else {
      response.put("mensaje", "Credenciales incorrectas");
      response.put("exitoso", false);
    }

    return response;
  }

  @Transactional
  public Map<String, String> addUserToCompany(String email, UUID targetCompanyId) {
    Map<String, String> response = new HashMap<>();

    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new RuntimeException("Usuario no encontrado");
    }

    Company company =
        companyRepository
            .findById(targetCompanyId)
            .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

    if (user.getCompany() != null) {
      response.put("mensaje", "El usuario ya pertenece a una compañía");
      return response;
    }

    user.setCompany(company);
    userRepository.save(user);

    response.put("mensaje", "El cliente se ha asignado a una compañia");
    return response;
  }

  @Transactional
  public Map<String, String> changeUserRole(String email, String newRole) {

    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new RuntimeException("Usuario no encontrado");
    }

    Rol role = rolService.getRolByName(newRole);
    if (role == null) {
      throw new RuntimeException("Rol no existe");
    }
    user.setRol(role);
    userRepository.save(user);

    return Map.of("mensaje", "Rol actualizado correctamente");
  }

  @Transactional
  public void addUserToGroup(String email, UUID groupId) {

    User user = userRepository.findByEmail(email);

    if (user == null) {
      throw new RuntimeException("Usuario no encontrado");
    }

    Group group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new RuntimeException("El usuario y el grupo pertenecen a compañías diferentes");
    }

    group.getUser().add(user);
    groupRepository.save(group);
  }

  public Page<Map<String, Object>> getEmployeeGroups(Pageable pageable) {
    User userContext =
        userRepository.findByEmail(
            SecurityContextHolder.getContext().getAuthentication().getName());
    Company company = userContext.getCompany();

    Page<User> userPage = userRepository.findAllByCompany(company, pageable);
    List<User> usersInPage = userPage.getContent();

    List<UUID> userIds = usersInPage.stream().map(User::getId).collect(Collectors.toList());

    List<Group> groups = groupRepository.findGroupsByUserIdsIn(userIds);

    Map<UUID, List<Group>> userGroupsMap = new HashMap<>();
    for (Group group : groups) {
      for (User user : group.getUser()) {
        UUID userId = user.getId();
        if (userIds.contains(userId)) {
          userGroupsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(group);
        }
      }
    }

    List<Map<String, Object>> clientsList = new ArrayList<>();
    for (User user : usersInPage) {
      Map<String, Object> userMap = new LinkedHashMap<>();
      userMap.put("fullName", user.getName() + " " + user.getLastName());
      userMap.put("Rol", user.getRol().getId().toString());
      userMap.put("email", user.getEmail());

      List<Map<String, Object>> groupsList =
          userGroupsMap.getOrDefault(user.getId(), Collections.emptyList()).stream()
              .map(
                  group -> {
                    Map<String, Object> groupMap = new LinkedHashMap<>();
                    groupMap.put("id", group.getId().toString());
                    groupMap.put("name", group.getName());
                    return groupMap;
                  })
              .collect(Collectors.toList());

      userMap.put("Groups", groupsList);
      clientsList.add(userMap);
    }

    return new PageImpl<>(clientsList, pageable, userPage.getTotalElements());
  }

  private String generateToken(String email) {
    return TokenUtilidades.createToken(Jwts.claims().subject(email).build());
  }
}
