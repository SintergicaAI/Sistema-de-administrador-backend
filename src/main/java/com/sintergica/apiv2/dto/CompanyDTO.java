package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.Rol;

import java.util.UUID;

public record CompanyDTO (UUID uuid, String name, String emailClient, Rol rol){
}
