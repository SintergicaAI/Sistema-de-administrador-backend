package com.sintergica.apiv2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sintergica.apiv2.entidades.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@JsonTypeName("user")
public class UserDTO {

  @JsonIgnore private UUID id;

  private String name;
  private String lastName;
  private String email;
  private Rol role;
  private List<GroupDTO> groups;

  public UserDTO(
      UUID id,
      String name,
      String lastName,
      @Email(message = "Correo no valido") @NotBlank(message = "Correo no puede estar vacio")
          String email,
      Rol role,
      List<GroupDTO> groups) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.role = role;
    this.groups = groups;
  }
}
