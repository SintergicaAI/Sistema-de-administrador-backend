package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.Rol;
import java.util.UUID;

public record LoginAndRegisterDTO(
    UUID uuid,
    String email,
    String name,
    String lastName,
    String token,
    String refreshToken,
    Rol rol) {}
