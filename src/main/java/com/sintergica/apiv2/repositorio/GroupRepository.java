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

  List<Group> findAllByCompany(Company company);

  Group findByCompanyAndName(Company company, String name);

  @Query(
      "SELECT g FROM Group g JOIN g.user u WHERE u.company = :company AND u.email = :email AND g.name ILIKE :groupName%")
  List<Group> findByCompanyAndUserEmailAndGroupNameStartingWith(
      @Param("company") Company company,
      @Param("email") String email,
      @Param("groupName") String groupName);

  @Query(
      "SELECT g FROM Group g JOIN g.user u JOIN u.company c WHERE g.name IN :groups AND c =:company")
  List<Group> findByNameAndGroups(List<String> groups, Company company);

  List<Group> findAllByNameContainingIgnoreCase(String name);
}
