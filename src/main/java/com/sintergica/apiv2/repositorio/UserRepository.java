package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  @Query(
      "SELECT u FROM User u WHERE CONCAT(u.name, ' ', u.lastName) ILIKE %:nameLastName% AND u.company = :company")
  List<User> findByNameAndCompany(String nameLastName, Company company);

  User findByEmail(String email);

  @Query(
      "SELECT DISTINCT u FROM User u "
          + "LEFT JOIN u.groups g "
          + "WHERE u.company = :company "
          + "AND u.isActive = :isActive "
          + "AND (:name IS NULL OR CONCAT(u.name, ' ', u.lastName) ILIKE %:name%) "
          + "AND (:groupIds IS NULL OR g.compositeKey IN :groupIds)")
  Page<User> findAllByCompanyAndIsActive(
      Company company, boolean isActive, String name, List<String> groupIds, Pageable pageable);

  @Query(
      "SELECT DISTINCT u FROM User u "
          + "LEFT JOIN u.groups g "
          + "WHERE u.company = :company "
          + "AND u.isActive = :isActive "
          + "AND (:name IS NULL OR CONCAT(u.name, ' ', u.lastName) ILIKE %:name%) "
          + "AND (:groupIds IS NULL OR g.compositeKey IN :groupIds)")
  List<User> findAllByCompanyAndIsActiveNotPageable(
      Company company, boolean isActive, String name, List<String> groupIds);

  @Query(
      "SELECT u FROM User u WHERE u.company = :company AND CONCAT(u.name, ' ', u.lastName) LIKE %:nameLastName%")
  Page<User> findByCompanyAndNameAndLastNameStartingWith(
      @Param("company") Company company,
      @Param("nameLastName") String nameLastName,
      Pageable pageable);

  Set<User> findByIdInAndIsActiveAndCompany(Set<UUID> inId, boolean isActive, Company company);
}
