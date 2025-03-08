package com.sintergica.apiv2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String last_name;
    private String token;
    private String refreshToken;
    private String role;
}
