package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.GroupKnowledge;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupKnowledgeRepository extends CrudRepository<GroupKnowledge, UUID> {
    Optional<GroupKnowledge> findByKnowledgeIdAndGroup_CompositeKey(UUID knowledgeId,String groupCompositeKey);
    void deleteByKnowledgeKey(int knowledgeKey);
}
