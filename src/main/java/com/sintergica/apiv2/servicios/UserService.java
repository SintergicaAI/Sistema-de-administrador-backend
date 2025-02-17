package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;
  private final RolService rolService;
  private final CompanyService companyService;
  private final GroupService groupService;

  public Map<String, String> registerUser(User user) {
    HashMap<String, String> response = new HashMap<>();

    if (isUserRegistered(user)) {
      response.put("Exito", "false");
      response.put("token", null);
      return response;
    }

    generateNewUser(user);
    userRepository.save(user);

    String token = generateToken(user.getEmail());
    response.put("Exito", "true");
    response.put("token", token);

    return response;
  }

  private boolean isUserRegistered(User user) {
    return userRepository.findByEmail(user.getEmail()) != null;
  }

  private void generateNewUser(User user) {
    user.setRol(rolService.getRolByName("GUEST"));
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

  public boolean loginUser(User userRequest) {
    User user = this.findByEmail(userRequest.getEmail());

    if (user == null) {
      return false;
    }

    if(!user.getPassword().equals(userRequest.getPassword())) {
      return false;
    }

    return true;
  }

  @Transactional
  public Map<String, String> addUserToCompany(String email, UUID targetCompanyId) {
    Map<String, String> response = new HashMap<>();

    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new RuntimeException("Usuario no encontrado");
    }

    Company company = companyService.getCompanyById(targetCompanyId).orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

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

    Group group = groupService.findGroupById(groupId).orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new RuntimeException("El usuario y el grupo pertenecen a compañías diferentes");
    }

    group.getUser().add(user);
    groupService.save(group);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }


  public String generateToken(String email) {
    return TokenUtils.createToken(Jwts.claims().subject(email).build());
  }
}
