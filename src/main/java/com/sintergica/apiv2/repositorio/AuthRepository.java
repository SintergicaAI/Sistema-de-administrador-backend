package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<AuthEntity, String> {
  AuthEntity findByEmail(String email);
}
