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

  public Group findGroupById(UUID uuidGroup) {

    if(groupRepository.findById(uuidGroup).isPresent()){
      return groupRepository.findById(uuidGroup).get();
    }

    throw new GroupNotFound("Grupo no encontrado");
  }

}
