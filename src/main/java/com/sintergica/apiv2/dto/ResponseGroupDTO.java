package com.sintergica.apiv2.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record ResponseGroupDTO(String groupKey,
                               String name,
                               Set<UserDataForGroupsDTO> userDTOS,
                               Date dateCreation,
                               Date dateEdit,
                               String createdBy) {
}
