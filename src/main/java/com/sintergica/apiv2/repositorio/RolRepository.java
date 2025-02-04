package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {
    Rol findByName(String name);
}
