package com.sintergica.apiv2.dto;

import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GroupDTO {

  private UUID id;
  private String name;

  public GroupDTO(UUID id, String name) {
    this.id = id;
    this.name = name;
  }
}
