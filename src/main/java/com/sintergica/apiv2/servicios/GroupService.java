package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.repositorio.GroupRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public void save(Group group) {
    this.groupRepository.save(group);
  }

  public Group findGroupById(UUID uuidGroup) {

    if (groupRepository.findById(uuidGroup).isPresent()) {
      return groupRepository.findById(uuidGroup).get();
    }

    throw new GroupNotFound("Grupo no encontrado");
  }
}
