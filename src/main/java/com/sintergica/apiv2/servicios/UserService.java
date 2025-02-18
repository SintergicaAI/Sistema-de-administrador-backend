package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;
  private final RolService rolService;
  private final CompanyService companyService;
  private final GroupService groupService;

  public boolean registerUser(User user) {

    this.findByEmail(user.getEmail());
    this.generateNewUser(user);
    this.userRepository.save(user);

    return true;
  }

  @Transactional
  public void addUserToCompany(String email, UUID targetCompanyId) {

    User user = this.findByEmail(email);
    Company company = companyService.getCompanyById(targetCompanyId);

    if(user.getCompany() != null){
      throw new CompanyUserConflict("El usuario ya tiene asociada una compañia");
    }

    user.setCompany(company);
    userRepository.save(user);
  }

  @Transactional
  public void changeUserRole(String email, String newRole) {

    User user = this.findByEmail(email);
    Rol role = rolService.getRolByName(newRole);
    user.setRol(role);

    userRepository.save(user);
  }

  @Transactional
  public void addUserToGroup(String email, UUID groupId) {

    User user = this.findByEmail(email);
    Group group = groupService.findGroupById(groupId);

    if (!user.getCompany().getId().equals(group.getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    group.getUser().add(user);
    groupService.save(group);
  }


  public Page<UserDTO> getEmployeeGroupsRemastered(Pageable pageable) {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = this.findByEmail(userName);
    Company company = this.companyService.getCompanyById(user.getCompany().getId());

    Page<User> users = this.userRepository.findAllByCompany(company, pageable);

    List<UserDTO> userDTOs = new ArrayList<>();

    for (User userGroup : users){

      List<GroupDTO> groupDTOList = new ArrayList<>();

      for (Group group : userGroup.getGroups()) {
        groupDTOList.add(new GroupDTO(group.getId(), group.getName()));
      }

      UserDTO userDTO = new UserDTO(userGroup.getId(), userGroup.getName(), userGroup.getLastName(), userGroup.getEmail(), groupDTOList);

      userDTOs.add(userDTO);
    }

    return new PageImpl<>(userDTOs, pageable, users.getTotalElements());
  }

  public User findByEmail(String email) {

    if(userRepository.findByEmail(email) == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

    return userRepository.findByEmail(email);
  }

  private void generateNewUser(User user) {
    user.setRol(rolService.getRolByName("GUEST"));
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

  public String generateToken(String email) {
    return TokenUtils.createToken(Jwts.claims().subject(email).build());
  }

}
