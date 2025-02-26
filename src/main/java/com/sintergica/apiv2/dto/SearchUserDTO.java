package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchUserDTO {

  private String email;
  private String username;
  private Rol rol;
  private int numberGroups;
}
