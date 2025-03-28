package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.GroupOverrideDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.entidades.views.*;
import com.sintergica.apiv2.exceptions.group.GroupConflict;
import com.sintergica.apiv2.repositorio.GroupRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.*;
import java.util.stream.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserService userService;
  private final CompanyGroupsViewService companyGroupsViewService;

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public List<Group> findGroupsByNameContainingIgnoreCase(String name) {
    return this.groupRepository.findAllByNameContainingIgnoreCase(name);
  }

  public List<Group> saveAll(List<Group> groups) {
    return groupRepository.saveAll(groups);
  }

  public Group save(Group group) {
    try {
      return this.groupRepository.save(group);
    } catch (Exception e) {
      throw new GroupConflict("El grupo ya existe " + e.getMessage());
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

  public Group deleteGroup(String name, Company company) {
    Group deleteGroup = this.groupRepository.findByCompanyAndName(company, name);

    deleteGroup.getUser().clear();
    this.groupRepository.save(deleteGroup);
    this.groupRepository.delete(deleteGroup);

    return deleteGroup;
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

  public GroupOverrideDTO overrideGroupsToUser(
      Company company, Collection<String> names, String emailUser) {

    User user = this.userService.findByEmail(emailUser);

    Set<Group> groupsWithoutUserTarget = new HashSet<>();
    GroupOverrideDTO oldStatus = new GroupOverrideDTO(user.getEmail(), new ArrayList<>());

    for (Group group : user.getGroups()) {
      oldStatus.groups().add(new GroupDTO(group.getId(),group.getName()+"-"+group.getCompany().getName(), group.getName()));
      group.getUser().remove(user);
      groupsWithoutUserTarget.add(group);
    }

    this.groupRepository.saveAll(groupsWithoutUserTarget);


    List<CompanyGroupsView> listOfGroups = companyGroupsViewService.findByIdCompanyAndCombinedNameIn(this.userService.getUserLogged().getCompany().getId(), names);
    Set<String> namesGroups = listOfGroups.stream().map(companyGroupsView -> companyGroupsView.getOriginalName()).collect(Collectors.toSet());

    Set<Group> groupsMatches = this.findByCompanyAndNameIn(company, namesGroups);

    for (Group group : groupsMatches) {
      group.getUser().add(user);
    }

    this.groupRepository.saveAll(groupsMatches);

    GroupOverrideDTO groupOverrideDTO = new GroupOverrideDTO(user.getEmail(), new ArrayList<>());
    for (Group groupUserIterator : groupsMatches) {
      groupOverrideDTO
          .groups()
          .add(new GroupDTO(groupUserIterator.getId(),groupUserIterator.getName()+"-"+groupUserIterator.getCompany().getName(), groupUserIterator.getName()));
    }

    return oldStatus;
  }

  public Set<Group> findByCompanyAndNameIn(Company company, Collection<String> names) {
    return this.groupRepository.findByCompanyAndNameIn(company, names);
  }

  public Set<Group> findByCompanyAndGroupNameStartingWithIgnoreCase(
      Company company, String groupName) {

    return this.groupRepository.findByCompanyAndGroupNameStartingWithIgnoreCase(company, groupName);
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
