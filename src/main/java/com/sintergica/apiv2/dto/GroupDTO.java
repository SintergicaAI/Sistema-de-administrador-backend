package com.sintergica.apiv2.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import lombok.Data;

@Data
@JsonTypeName("group")
public class GroupDTO {

  private UUID  id;
  private String compositeKey;
  private String name;

  public GroupDTO(UUID id, String compositeKey,String name) {
    this.id = id;
    this.compositeKey = compositeKey;
    this.name = name;
  }
}
