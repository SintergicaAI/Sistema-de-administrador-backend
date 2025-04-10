package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResetPasswordTokensRepository extends JpaRepository<ResetPasswordTokens, UUID> {
    ResetPasswordTokens findByToken(UUID token);
    ResetPasswordTokens findByUserAndIsUsed(User user, boolean isUsed);
}
