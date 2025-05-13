package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.GroupOverrideDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.GroupKnowledge;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.group.GroupConflict;
import com.sintergica.apiv2.repositorio.GroupKnowledgeRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.utilidades.KeyGenerator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupKnowledgeRepository groupKnowledgeRepository;
  private final UserService userService;

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
      throw new GroupConflict("El grupo ya existe ", e);
    }
  }

  public boolean existsByCompositeKey(String compositeKey) {
    return this.groupRepository.existsByCompositeKey(compositeKey);
  }

  public Group addGroupWithUniqueKey(Group group) {
    String key;
    int trys = 0;

    do {
      trys++;
      if (trys > 5) {
        throw new RuntimeException("No se pudo generar clave única");
      }
      String keyGroup = group.getCompositeKey();
      key = keyGroup + KeyGenerator.generateShortId();
    } while (existsByCompositeKey(key));

    group.setCompositeKey(key);
    return groupRepository.save(group);
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

  public Group deleteGroup(String keyComposite, Company company) {
    Group deleteGroup = this.groupRepository.findByCompanyAndCompositeKey(company, keyComposite);

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

  public Group findByCompanyAndCompositeKey(Company company, String compositeKey) {
    return this.groupRepository.findByCompanyAndCompositeKey(company, compositeKey);
  }

  public GroupOverrideDTO overrideGroupsToUser(
      Company company, Collection<String> compositeKeys, String emailUser) {

    User user = this.userService.findByEmail(emailUser);

    Set<Group> groupsWithoutUserTarget = new HashSet<>();
    GroupOverrideDTO oldStatus = new GroupOverrideDTO(user.getEmail(), new ArrayList<>());

    for (Group group : user.getGroups()) {
      oldStatus.groups().add(new GroupDTO(group.getCompositeKey(), group.getName()));
      group.getUser().remove(user);
      groupsWithoutUserTarget.add(group);
    }

    this.groupRepository.saveAll(groupsWithoutUserTarget);

    Set<Group> groupsMatches = this.findByCompanyAndCompositeKeyIn(company, compositeKeys);

    for (Group group : groupsMatches) {
      group.getUser().add(user);
    }

    this.groupRepository.saveAll(groupsMatches);

    GroupOverrideDTO groupOverrideDTO = new GroupOverrideDTO(user.getEmail(), new ArrayList<>());
    for (Group groupUserIterator : groupsMatches) {
      groupOverrideDTO
          .groups()
          .add(new GroupDTO(groupUserIterator.getCompositeKey(), groupUserIterator.getName()));
    }

    return oldStatus;
  }

  public Set<Group> findByCompanyAndCompositeKeyIn(Company company, Collection<String> names) {
    return this.groupRepository.findByCompanyAndCompositeKeyIn(company, names);
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

  /**
   * @author Panther
   * @param companyId
   * @return
   */
  public List<Group> findAllByCompanyID(UUID companyId) {
    return this.groupRepository.findGroupsByCompany_Id(companyId);
  }

  /**
   * @author Panther
   * @param companyId
   */
  public List<Group> removeAllUsersFromGroupsByCompany(UUID companyId) {
    List<Group> groups = this.groupRepository.findGroupsByCompany_Id(companyId);
    for (Group group : groups) {
      group.getUser().clear();
    }

    return this.saveAll(groups); // throw new CompanyNotFound("Company Not Found");
  }

  /**
   * @author Panther
   * @param companyId
   */
  public void deleteAllCompanyGroups(UUID companyId) {
    // TODO
    List<Group> groups = this.removeAllUsersFromGroupsByCompany(companyId);
    this.groupRepository.deleteAll(groups);
    // throw new CompanyNotFound("Company not found");
  }

  /**
   * @author Panther
   * @param group
   * @param knowledgeId
   * @return
   */
  public GroupKnowledge addKnowledge(Group group, UUID knowledgeId) {
    GroupKnowledge groupKnowledge = new GroupKnowledge();
    groupKnowledge.setKnowledgeId(knowledgeId);
    groupKnowledge.setGroup(group);
    return groupKnowledgeRepository.save(groupKnowledge);
  }

  /**
   * @author Panther
   * @param group
   * @param knowledgeId
   * @return
   */
  public boolean deleteKnowledge(Group group, UUID knowledgeId) {
    Optional<GroupKnowledge> groupKnowledge =
        groupKnowledgeRepository.findByKnowledgeIdAndGroup_CompositeKey(
            knowledgeId, group.getCompositeKey());
    if (groupKnowledge.isPresent()) {
      groupKnowledgeRepository.deleteByKnowledgeKey(groupKnowledge.get().getKnowledgeKey());
      return true;
    }
    return false;
  }
}
