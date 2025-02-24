package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  User findByEmail(String email);

  Page<User> findAllByCompany(Company company, Pageable pageable);

  Page<User> findByName(String name, Pageable pageable);

  @Query(
      "SELECT u FROM User u WHERE "
          + "u.company = :company AND "
          + "(:name IS NULL OR :name = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<User> findByCompanyAndName(
      @Param("company") Company company, @Param("name") String name, Pageable pageable);
}
