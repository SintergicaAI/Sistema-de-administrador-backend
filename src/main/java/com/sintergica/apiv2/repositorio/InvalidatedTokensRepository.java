package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.InvalidatedTokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokensRepository extends JpaRepository<InvalidatedTokens, String> {

  InvalidatedTokens findByRefreshToken(String token);
}
