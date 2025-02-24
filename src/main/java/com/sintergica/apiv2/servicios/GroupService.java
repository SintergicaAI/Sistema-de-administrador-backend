package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.repositorio.GroupRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserService userService;

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public void save(Group group) {
    this.groupRepository.save(group);
  }

  public Group findGroupById(UUID uuidGroup) {
    return groupRepository
        .findById(uuidGroup)
        .orElseThrow(() -> new GroupNotFound("Grupo no encontrado"));
  }

  @Transactional
  public Group addUser(String email, UUID groupId) {

    User user = this.userService.findByEmail(email);
    Optional<Group> group = this.groupRepository.findById(groupId);

    group.orElseThrow(() -> new GroupNotFound("Grupo no encontrado"));

    if (!user.getCompany().getId().equals(group.get().getCompany().getId())) {
      throw new CompanyUserConflict("El usuario o el grupo no tienen asociados la misma empresa");
    }

    group.get().getUser().add(user);
    return this.groupRepository.save(group.get());
  }
}
