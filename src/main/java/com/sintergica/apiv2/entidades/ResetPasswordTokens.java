package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reset_password_tokens")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ResetPasswordTokens {

    @Id private UUID token;

    @ManyToOne
    @JoinColumn(name = "userFk")
    private User user;

    @Column(name = "expireDate")
    private LocalDateTime expireDate;

    @Column
    private boolean isUsed;


}
