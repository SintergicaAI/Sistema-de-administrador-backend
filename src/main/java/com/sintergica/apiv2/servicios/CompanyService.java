package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

  private List<UserDTO> parserUserToUserDTOAndGroupToList(List<User> users) {
    return users.stream()
        .map(
            user ->
                new UserDTO(
                    user.getId(),
                    user.getName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRol(),
                    user.getGroups().stream()
                        .map(group -> new GroupDTO(group.getId(), group.getName()))
                        .collect(Collectors.toList())))
        .collect(Collectors.toList());
  }

  public Page<UserDTO> getUsersByCompanyAndOptionalUsername(
      Company company, String username, List<UUID> groups, int size, int page) {

    if (size == -1 || page == -1) {
      List<User> users =
          this.userService.findAllByCompanyAndIsActiveNotPageable(company, true, username, groups);

      List<UserDTO> data = parserUserToUserDTOAndGroupToList(users);

      int sizeList = users.isEmpty() ? 1 : users.size();

      return new PageImpl<>(data, PageRequest.of(0, sizeList), sizeList);
    }

    Pageable pageable = PageRequest.of(page, size);

    Page<User> users =
        this.userService.findAllByCompanyAndIsActive(company, true, username, groups, pageable);

    List<UserDTO> data = parserUserToUserDTOAndGroupToList(users.getContent());

    return new PageImpl<>(data, pageable, users.getTotalElements());
  }

  public Page<UserDTO> getGroupsByCompanyAndOptionalUsername(
      List<String> groupNames, int size, int page) {
    Company userLogCompany = userService.getUserLogged().getCompany();

    Set<Group> groupsAssociateWithCompany =
        searchGroupsAssociateWithCompany(userLogCompany, groupNames);

    List<UUID> uuidListToGroupsAssociatedWithCompany =
        groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toList());

    return this.getUsersByCompanyAndOptionalUsername(
        this.userService.getUserLogged().getCompany(),
        null,
        uuidListToGroupsAssociatedWithCompany,
        size,
        page);
  }

  private Set<Group> searchGroupsAssociateWithCompany(Company company, List<String> groupNames) {
    Company userLogCompany = company;
    Set<Group> groupsAssociateWithCompany = new HashSet<>();

    /*
    Function<String, Set<Group>> functionInteractiveWithDataBase = s -> groupService.findByCompanyAndGroupNameStartingWithIgnoreCase(userLogCompany, s);
    Function<Set<Group>, Stream<Group>> flatting = groups -> groups.stream();
    List<Group> matchersGroups = groupNames.stream().map(functionInteractiveWithDataBase).flatMap(flatting).collect(Collectors.toList());
    */

    for (String groupName : groupNames) {
      Set<Group> groupsMatch =
          this.groupService.findByCompanyAndGroupNameStartingWithIgnoreCase(
              userLogCompany, groupName);
      groupsAssociateWithCompany.addAll(groupsMatch);
    }

    return groupsAssociateWithCompany;
  }

  public Page<UserDTO> getUsersByCompanyAndUsernameAndGroupsName(
      String userName, List<String> groupNames, int size, int page) {

    Company userLogCompany = userService.getUserLogged().getCompany();
    Set<Group> groupsAssociateWithCompany =
        searchGroupsAssociateWithCompany(userLogCompany, groupNames);

    List<UUID> uuidListToGroupsAssociatedWithCompany =
        groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toList());

    Page<UserDTO> resultClients =
        this.getUsersByCompanyAndOptionalUsername(
            this.userService.getUserLogged().getCompany(),
            userName,
            uuidListToGroupsAssociatedWithCompany,
            size,
            page);

    List<UserDTO> resultClientsList = new ArrayList<>(resultClients.getContent());
    Iterator<UserDTO> iterator = resultClientsList.iterator();

    while (iterator.hasNext()) {
      UserDTO user = iterator.next();
      Set<UUID> uuidValidList =
          groupsAssociateWithCompany.stream().map(Group::getId).collect(Collectors.toSet());
      user.getGroups().removeIf(groupDTO -> !uuidValidList.contains(groupDTO.getId()));
    }

    Pageable pegeable =
        size == -1 || page == -1
            ? PageRequest.of(0, resultClients.getSize())
            : PageRequest.of(page, size);

    return new PageImpl<>(resultClientsList, pegeable, resultClients.getTotalElements());
  }

  public Optional<Company> findById(UUID uuid) {
    return this.companyRepository.findById(uuid);
  }
}
