package com.sintergica.apiv2.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class GroupKnowledgeDTO {
  private String group_id;
  private UUID knowledgeId;
}
