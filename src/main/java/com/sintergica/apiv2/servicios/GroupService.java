package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.GroupRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserService userService;

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public List<Group> findGroupsByNameContainingIgnoreCase(String name) {
    return this.groupRepository.findAllByNameContainingIgnoreCase(name);
  }

  public Group save(Group group) {

    try {
      return this.groupRepository.save(group);
    } catch (Exception e) {
      return null;
    }
  }

  public Group findGroupById(UUID uuidGroup) {
    return groupRepository.findById(uuidGroup).get();
  }

  public Optional<Group> findById(UUID uuidGroup) {
    return groupRepository.findById(uuidGroup);
  }

  public List<Group> findGroupByCompany(Company company) {
    return this.groupRepository.findAllByCompany(company);
  }

  @Transactional
  public Group addUser(User user, Group group) {

    group.getUser().add(user);
    return this.groupRepository.save(group);
  }

  @Transactional
  public Group deleteUser(Group group, User user) {
    group.getUser().remove(user);
    return this.groupRepository.save(group);
  }

  @Transactional
  public Group findGroupByCompanyAndName(Company company, String name) {
    return this.groupRepository.findByCompanyAndName(company, name);
  }

  public List<Group> findByCompanyAndUserEmailAndGroupNameStartingWith(
      Company company, String email, String groupName) {
    return this.groupRepository.findByCompanyAndUserEmailAndGroupNameStartingWith(
        company, email, groupName);
  }

  public List<Group> findByNameAndGroups(List<String> groups, Company company) {
    return this.groupRepository.findByNameAndGroups(groups, company);
  }
}
