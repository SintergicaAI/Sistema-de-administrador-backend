package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Grant;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GrantRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final GrantRepository grantRepository;

  public GroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      CompanyRepository companyRepository,
      GrantRepository grantRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.grantRepository = grantRepository;
  }

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public Map<String, Boolean> save(Group group) {
    Optional<Group> groupOptional = this.groupRepository.findById(group.getId());
    HashMap<String, Boolean> response = new HashMap<>();

    if (groupOptional.isPresent()) {
      response.put("error", false);
      return response;
    }

    response.put("success", true);
    this.groupRepository.save(group);
    return response;
  }

  public Map<String, String> addUserToGroup(UUID groupId, String emailUser) {
    Optional<Group> currentGroupOpt = groupRepository.findById(groupId);
    User currentUser = userRepository.findByEmail(emailUser);

    if (currentGroupOpt.isPresent()
        && currentUser != null
        && currentUser.getCompany() != null
        && currentGroupOpt.get().getCompany() != null
        && currentUser.getCompany().getId().equals(currentGroupOpt.get().getCompany().getId())) {

      Group currentGroup = currentGroupOpt.get();
      currentGroup.getUser().add(currentUser);
      groupRepository.save(currentGroup);

      HashMap<String, String> response = new HashMap<>();
      response.put("success", "Usuario agregado al grupo");
      return response;
    }

    HashMap<String, String> response = new HashMap<>();
    response.put(
        "error", "Uno de los campos es nulo o la empresa no esta asociada con el usuario y grupo");
    return response;
  }

  public Map<String, String> addNewGroup(
      String nameGroup, List<String> grantList, UUID companyUUID) {
    HashMap<String, String> response = new HashMap<>();
    Optional<Company> company = companyRepository.findById(companyUUID);

    if (company.isEmpty()) {
      response.put("error", "La compañia no existe");
      return response;
    }

    Set<Grant> grants =
        grantList.stream()
            .map(grantRepository::findByName)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Group newGroup = new Group();
    newGroup.setName(nameGroup);
    newGroup.setCompany(company.get());
    newGroup.setGrant(grants);
    groupRepository.save(newGroup);

    response.put("success", "Grupo agregado");
    return response;
  }

  public Map<String, String> deleteUserFromGroup(UUID uuidGroup, String emailClient) {
    HashMap<String, String> response = new HashMap<>();

    Optional<Group> group = groupRepository.findById(uuidGroup);
    User user = userRepository.findByEmail(emailClient);

    if (group.isEmpty()) {
      response.put("error", "Grupo no encontrado");
      return response;
    }

    if (user == null) {
      response.put("error", "Usuario no encontrado");
      return response;
    }

    Group referenceGroup = group.get();
    if (!referenceGroup.getUser().contains(user)) {
      response.put("error", "El usuario no pertenece al grupo");
      return response;
    }

    referenceGroup.getUser().remove(user);
    groupRepository.save(referenceGroup);

    response.put("success", "Usuario eliminado correctamente");
    return response;
  }

  public Map<String, String> changeGrants(UUID uuidGroup, List<String> newGrants) {

    Optional<Group> optionalGroup = this.groupRepository.findById(uuidGroup);
    HashMap<String, String> response = new HashMap<>();
    if (optionalGroup.isPresent()) {

      Set<Grant> grantList = optionalGroup.get().getGrant();

      if (grantList != null) {
        grantList.clear();

        for (Grant grant : grantList) {
          Grant cachedGrant = grantRepository.findByName(grant.getName());
          grantList.add(cachedGrant);
        }
      }

      optionalGroup.get().setGrant(grantList);
      groupRepository.save(optionalGroup.get());
    }

    response.put("success", "Grupo agregado");

    return response;
  }
}
