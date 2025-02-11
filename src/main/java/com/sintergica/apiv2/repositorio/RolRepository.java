package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Rol;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {
  Rol findByName(String name);
}
