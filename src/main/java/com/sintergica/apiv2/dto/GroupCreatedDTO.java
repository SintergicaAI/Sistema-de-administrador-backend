package com.sintergica.apiv2.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record GroupCreatedDTO(
    String groupKey, String name, Set<UUID> users, Date dateCreation, Date dateEdit, String createdBy) {}
