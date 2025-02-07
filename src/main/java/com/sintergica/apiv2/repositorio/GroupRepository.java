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

  @Query(
      "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Group g JOIN g.user u WHERE g.id = :groupId AND u.id = :userId")
  boolean existsUserInGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

  List<Group> findAllByCompany(Company company);
}
