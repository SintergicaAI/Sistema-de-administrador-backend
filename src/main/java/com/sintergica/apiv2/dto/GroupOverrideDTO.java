package com.sintergica.apiv2.dto;

import java.util.List;

public record GroupOverrideDTO(String userEmail, List<GroupDTO> groups) {
}
