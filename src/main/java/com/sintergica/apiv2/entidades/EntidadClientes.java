package com.sintergica.apiv2.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class EntidadClientes {

    @Id
    @Email(message = "Correo no valido")
    @NotBlank(message = "Correo no puede estar vacio")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    private String contrasena;

    private int edad;

    private String nombre;

    private String apellido_paterno;

    private String apellido_materno;

    private String rol;

    @Override
    public String toString() {
        return correo +" "+ contrasena;
    }

}