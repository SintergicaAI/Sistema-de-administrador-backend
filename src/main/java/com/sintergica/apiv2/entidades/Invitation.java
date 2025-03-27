package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "list_invitations")
@Data
public class Invitation {
  @Id private UUID token;

  @Column(name = "email")
  private String email;

  @Column(name = "isActive")
  private boolean isActive;

  @Column(name = "expireDate")
  private LocalDateTime expireDate;

  @ManyToOne
  @JoinColumn(name = "groupId")
  private Group group;
}
