package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  User findByEmail(String email);
}
