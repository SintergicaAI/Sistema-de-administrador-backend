package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Date;
import lombok.*;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class InvalidatedTokens {

  @Id private String refreshToken;

  @OneToOne
  @JoinColumn(name = "userFk")
  private User user;

  @Column private Date invalidationDate;

  @Column private Date expirationDate;
}
