package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.Rol;

public record RolUserDTO(String email, String name, String lastName, Rol rol) {}
