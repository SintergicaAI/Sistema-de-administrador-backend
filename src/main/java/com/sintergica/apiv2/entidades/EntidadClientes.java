package com.sintergica.apiv2.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class EntidadClientes {

    @Id
    @Email(message = "Correo no valido")
    @NotBlank(message = "Correo no puede estar vacio")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    private String contrasena;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public String toString() {
        return correo +" "+ contrasena;
    }

}