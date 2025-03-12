package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final UserService userService;
  private final GroupService groupService;

  public List<Company> findAll() {
    return this.companyRepository.findAll();
  }

  public Company add(Company company) {
    return this.companyRepository.save(company);
  }

  public Company getCompanyById(UUID uuid) {
    return this.companyRepository.findById(uuid).get();
  }

  @Transactional
  public User addUserToCompany(User user, Company company) {
    user.setCompany(company);
    return this.userService.save(user);
  }

  @Transactional
  public User deleteUserFromCompany(User userFound) {
    userFound.setActive(false);
    return this.userService.save(userFound);
  }

  public Page<UserDTO> getUsersByCompanyAndOptionalUsername(
      Company company, String username, List<UUID> groups, Pageable pageable) {
    Page<User> users =
        this.userService.findAllByCompanyAndIsActive(company, true, username, groups, pageable);
    List<UserDTO> data =
        users.stream()
            .map(
                user ->
                    new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getGroups().stream()
                            .map(group -> new GroupDTO(group.getId(), group.getName()))
                            .collect(Collectors.toList())))
            .collect(Collectors.toList());
    return new PageImpl<>(data, pageable, users.getTotalElements());
  }

  public Page<UserDTO> getGroupsByCompanyAndOptionalUsername(
      List<String> groupNames, Pageable pegeable) {

    Company userLogCompany = this.userService.getUserLogged().getCompany();

    Set<Group> groupsAssociateWithCompany =
        groupNames.stream()
            .flatMap(
                groupName ->
                    groupService
                        .findByCompanyAndGroupNameStartingWithIgnoreCase(userLogCompany, groupName)
                        .stream())
            .collect(Collectors.toSet());
    /*List<Group> groupsAssociateWithCompany =
        this.groupService.findByNameAndGroups(groupNames, userService.getUserLogged().getCompany());
    */
    List<UUID> uuidListToGroupsAssociatedWithCompany =
        groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toList());
    return this.getUsersByCompanyAndOptionalUsername(
        this.userService.getUserLogged().getCompany(),
        null,
        uuidListToGroupsAssociatedWithCompany,
        pegeable);
  }

  public Page<UserDTO> getUsersByCompanyAndUsernameAndGroupsName(
      String userName, List<String> groupNames, Pageable pegeable) {

    Company userLogCompany = this.userService.getUserLogged().getCompany();

    Set<Group> groupsAssociateWithCompany =
        groupNames.stream()
            .flatMap(
                groupName ->
                    groupService
                        .findByCompanyAndGroupNameStartingWithIgnoreCase(userLogCompany, groupName)
                        .stream())
            .collect(Collectors.toSet());

    /*List<Group> groupsAssociateWithCompany =
        groupService.findByNameAndGroups(groupNames, userService.getUserLogged().getCompany());
    */
    List<UUID> uuidListToGroupsAssociatedWithCompany =
        groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toList());

    Page<UserDTO> resultClients =
        this.getUsersByCompanyAndOptionalUsername(
            this.userService.getUserLogged().getCompany(),
            userName,
            uuidListToGroupsAssociatedWithCompany,
            pegeable);

    List<UserDTO> resultClientsList = new ArrayList<>(resultClients.getContent());
    Iterator<UserDTO> iterator = resultClientsList.iterator();

    while (iterator.hasNext()) {
      UserDTO user = iterator.next();
      Set<UUID> uuidValidList =
          groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toSet());
      user.getGroups().removeIf(groupDTO -> !uuidValidList.contains(groupDTO.getId()));
    }
    return new PageImpl<>(resultClientsList, pegeable, resultClients.getTotalElements());
  }

  public Optional<Company> findById(UUID uuid) {
    return this.companyRepository.findById(uuid);
  }
}
