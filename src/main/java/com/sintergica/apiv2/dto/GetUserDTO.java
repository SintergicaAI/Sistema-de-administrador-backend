package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.*;

public record GetUserDTO(String email, String name, String lastName, Rol rol) {}

