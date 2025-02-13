package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

  Group findByName(String name);

  List<Group> findAllByCompany(Company company);

  @Query("SELECT DISTINCT g FROM Group g JOIN FETCH g.user u WHERE u.id IN :userIds")
  List<Group> findGroupsByUserIdsIn(@Param("userIds") List<UUID> userIds);
}
