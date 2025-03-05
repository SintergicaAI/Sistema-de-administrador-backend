package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;

import java.util.Date;
import lombok.*;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class InvalidatedTokens {

  @Id private String refreshToken;

  @ManyToOne
  @JoinColumn(name = "userFk")
  private User user;

  @Column private Date invalidationDate;

  @Column private Date expirationDate;
}
