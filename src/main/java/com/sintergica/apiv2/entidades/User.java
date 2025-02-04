package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "listUser")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Email(message = "Correo no valido")
    @NotBlank(message = "Correo no puede estar vacio")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    private String password;

    private String name;

    private String firstName;

    private String lastName;

    @ManyToOne
    @JoinColumn(name = "rol")
    private Rol rol;

    @ManyToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Group> userGroups;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "grupo_clientes",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<Group> groups = new HashSet<>();*/

    @Override
    public String toString() {
        return email +" "+ password;
    }

}