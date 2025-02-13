package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "list_invitations")
@Data
public class Invitation {
    @Id
    private UUID token;

    @Column(name = "email")
    private String email;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "expireDate")
    private LocalDateTime expireDate;
}

