package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
