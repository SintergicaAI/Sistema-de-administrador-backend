package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;

// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "listUser")
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Email(message = "Correo no valido")
  @NotBlank(message = "Correo no puede estar vacio")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacia")
  @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
  private String password;

  @Column(name = "name")
  private String name;

  @Column(name = "lastName")
  private String lastName;

  @ManyToOne
  @JoinColumn(name = "rolId")
  private Rol rol;

  @ManyToOne
  @JoinColumn(name = "companyId")
  private Company company;
}
