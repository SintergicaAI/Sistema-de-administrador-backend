package com.sintergica.apiv2.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class InvitationDTO {
  private UUID token;

  private String email;

  private boolean isActive;

  private LocalDateTime expireDate;
}
