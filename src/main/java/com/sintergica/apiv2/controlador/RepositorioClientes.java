package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.EntidadClientes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioClientes extends JpaRepository<EntidadClientes, String> {
    boolean existsByCorreoAndContrasena(String correo, String contrasena);
}