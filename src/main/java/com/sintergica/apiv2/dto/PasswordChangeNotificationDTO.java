package com.sintergica.apiv2.dto;

import java.time.*;

public record PasswordChangeNotificationDTO(String email, String uuid, LocalDateTime expiredAt) {
}
