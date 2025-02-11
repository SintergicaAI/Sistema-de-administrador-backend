package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "role")
@Data
public class Rol {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "name")
  private String name;
}
