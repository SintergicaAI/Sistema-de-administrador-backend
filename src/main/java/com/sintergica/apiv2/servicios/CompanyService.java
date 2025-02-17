package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Data
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;

  private final GroupService groupService;
  private final UserRepository userRepository;

  public List<Company> findAll() {
    return this.companyRepository.findAll();
  }

  public Map<String, Boolean> add(Company company) {
    Optional<Company> optionalCompany = this.companyRepository.findById(company.getId());

    HashMap<String, Boolean> response = new HashMap<>();

    if (optionalCompany.isPresent()) {
      response.put("error", false);
      return response;
    }

    response.put("success", true);
    this.companyRepository.save(company);
    return response;
  }

  public Map<String, Company> getCompanyByUUID(UUID uuid) {
    HashMap<String, Company> response = new HashMap<>();

    Optional<Company> companyOptional = companyRepository.findById(uuid);

    if (companyOptional.isPresent()) {
      response.put("success", companyOptional.get());
      return response;
    }

    response.put("error", null);

    return response;
  }

  public Page<UserDTO> getEmployeeGroupsRemastered(Pageable pageable) {
    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(userName);

    if (user.getCompany() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El usuario no tiene ninguna organización asignada");
    }

    Company company = this.getCompanyById(user.getCompany().getId()).get();

    Page<User> users = userRepository.findAllByCompany(company, pageable);

    ArrayList<UUID> uuidUsers = new ArrayList<>();
    List<User> usersPage = users.getContent();

    for (User userTemp : usersPage) {
      uuidUsers.add(userTemp.getId());
    }

    List<Group> groups = groupService.findGroupsByUserIdsIn(uuidUsers);

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

  public Page<UserDTO> searchUser(String userName, Pageable pageable) {

    String userNameAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(userNameAdmin);

    if (user.getCompany() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El usuario no tiene ninguna organización asignada");
    }

    Company company = this.getCompanyById(user.getCompany().getId()).get();

    Page<User> users = this.userRepository.findByCompanyAndName(company, userName, pageable);

    ArrayList<UUID> uuidUsers = new ArrayList<>();
    List<User> usersPage = users.getContent();

    for (User userTemp : usersPage) {
      uuidUsers.add(userTemp.getId());
    }

    List<Group> groups = groupService.findGroupsByUserIdsIn(uuidUsers);

    List<UserDTO> listDTO = generatorUserDTOList(groups, users);

    return new PageImpl<>(listDTO, pageable, users.getTotalElements());
  }

  public Optional<Company> getCompanyById(UUID uuid) {
    return this.companyRepository.findById(uuid);
  }
}
