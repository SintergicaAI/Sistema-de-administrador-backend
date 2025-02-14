package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final GroupRepository groupRepository;
  private final PasswordEncoder passwordEncoder;
  private final RolRepository rolRepository;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      RolRepository rolRepository,
      CompanyRepository companyRepository,
      GroupRepository groupRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.rolRepository = rolRepository;
    this.companyRepository = companyRepository;
    this.groupRepository = groupRepository;
  }

  public Map<String, String> registerUser(User user) {
    HashMap<String, String> response = new HashMap<>();

    user.setRol(rolRepository.findByName("GUEST"));
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (userRepository.findByEmail(user.getEmail()) != null) {
      response.put("Exito", "false");
      response.put("token", null);
      return response;
    }

    userRepository.save(user);
    String token = generateToken(user.getEmail());
    response.put("Exito", "true");
    response.put("token", token);

    return response;
  }

  public Map<String, String> loginUser(User userRequest) {
    Map<String, String> response = new HashMap<>();

    User user = userRepository.findByEmail(userRequest.getEmail());

    if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
      String token = generateToken(user.getEmail());
      response.put("mensaje", "Bienvenido");
      response.put("exitoso", "true");
      response.put("token", token);
    } else {
      response.put("mensaje", "Credenciales incorrectas");
      response.put("exitoso", "false");
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

    Rol role = rolRepository.findByName(newRole);
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

  public Page<UserDTO> getEmployeeGroupsRemastered(Pageable pageable) {
    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(userName);

    if (user.getCompany() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El usuario no tiene ninguna organización asignada");
    }

    Company company = companyRepository.findById(user.getCompany().getId()).get();

    Page<User> users = userRepository.findAllByCompany(company, pageable);

    ArrayList<UUID> uuidUsers = new ArrayList<>();
    List<User> usersPage = users.getContent();

    for (User userTemp : usersPage) {
      uuidUsers.add(userTemp.getId());
    }

    List<Group> groups = groupRepository.findGroupsByUserIdsIn(uuidUsers);

    List<UserDTO> listDTO = generatorUserDTOList(groups, users);

    return new PageImpl<>(listDTO, pageable, users.getTotalElements());
  }

  private List<UserDTO> generatorUserDTOList(List<Group> groups, Page<User> users) {
    List<UserDTO> listDTO = new ArrayList<>();

    for (Group group : groups) {
      for (User userTemp : group.getUser()) {

        Optional<UserDTO> foundUser =
            listDTO.stream().filter(usere -> usere.getId().equals(userTemp.getId())).findFirst();

        if (foundUser.isPresent()) {
          UserDTO userDTO = foundUser.get();
          GroupDTO groupDTO = new GroupDTO(group.getId(), group.getName());

          userDTO.getGroupDTOList().add(groupDTO);

        } else {
          UserDTO userDTO =
              new UserDTO(
                  userTemp.getId(),
                  userTemp.getName(),
                  userTemp.getLastName(),
                  userTemp.getEmail());
          userDTO.getGroupDTOList().add(new GroupDTO(group.getId(), group.getName()));

          listDTO.add(userDTO);
        }
      }
    }

    balanceList(listDTO, users);

    return listDTO;
  }

  private void balanceList(List<UserDTO> listDTO, Page<User> users) {
    Set<UUID> existingIds = listDTO.stream().map(UserDTO::getId).collect(Collectors.toSet());
    List<User> usersInPage = users.getContent();

    for (User user : usersInPage) {
      if (!existingIds.contains(user.getId())) {
        UserDTO newUser =
            new UserDTO(user.getId(), user.getName(), user.getLastName(), user.getEmail());
        listDTO.add(newUser);
        existingIds.add(user.getId());
      }
    }
  }

  public Page<UserDTO> searhUser(String userName, Pageable pageable) {

    String userNameAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(userNameAdmin);

    if (user.getCompany() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El usuario no tiene ninguna organización asignada");
    }

    Company company = companyRepository.findById(user.getCompany().getId()).get();

    Page<User> users = userRepository.findByCompanyAndName(company, userName, pageable);

    ArrayList<UUID> uuidUsers = new ArrayList<>();
    List<User> usersPage = users.getContent();

    for (User userTemp : usersPage) {
      uuidUsers.add(userTemp.getId());
    }

    List<Group> groups = groupRepository.findGroupsByUserIdsIn(uuidUsers);

    List<UserDTO> listDTO = generatorUserDTOList(groups, users);

    return new PageImpl<>(listDTO, pageable, users.getTotalElements());
  }

  private String generateToken(String email) {
    return TokenUtils.createToken(Jwts.claims().subject(email).build());
  }
}
