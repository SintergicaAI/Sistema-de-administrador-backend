package com.sintergica.apiv2.dto;

import java.util.Set;

public record DataUserForGroupDTO(String name,
                                  Set<String> email) { }
