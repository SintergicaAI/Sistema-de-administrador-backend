package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.GroupKnowledge;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface GroupKnowledgeRepository extends CrudRepository<GroupKnowledge, UUID> {
  Optional<GroupKnowledge> findByKnowledgeIdAndGroup_CompositeKey(
      UUID knowledgeId, String groupCompositeKey);

  void deleteByKnowledgeKey(int knowledgeKey);
}
