package com.sintergica.apiv2.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GroupKnowledgeDTO {
    private String group_id;
    private UUID knowledgeId;
}
