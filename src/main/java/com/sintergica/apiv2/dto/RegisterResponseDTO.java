package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
  private UUID id;
  private String email;
  private String name;
  private String lastName;
  private String token;
  private String refreshToken;
  private String role;
}
