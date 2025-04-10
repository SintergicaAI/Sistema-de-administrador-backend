package com.sintergica.apiv2.dto;


import java.util.UUID;

public record ChangePasswordDTO(UUID token, String password) {
}
