package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Invitation findByExpireDate(Date expireDate);
}
