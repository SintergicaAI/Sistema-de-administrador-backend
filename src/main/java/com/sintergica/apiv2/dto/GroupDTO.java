package com.sintergica.apiv2.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import lombok.Data;

@Data
@JsonTypeName("group")
public class GroupDTO {

  private String group_id;
  private String name;

  public GroupDTO(String group_id, String name) {
    this.group_id = group_id;
    this.name = name;
  }
}
