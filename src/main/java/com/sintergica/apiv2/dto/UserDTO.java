package com.sintergica.apiv2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDTO {

  @JsonIgnore private UUID id;

  private String name;
  private String lastName;
  private String email;
  private List<GroupDTO> groupDTOList;

  public UserDTO(
      UUID id,
      String name,
      String lastName,
      @Email(message = "Correo no valido") @NotBlank(message = "Correo no puede estar vacio")
          String email,
      ArrayList<Object> objects) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.groupDTOList = new ArrayList<>();
  }
}
