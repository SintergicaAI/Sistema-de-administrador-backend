package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
  UserEntity findByEmail(String email);
}
