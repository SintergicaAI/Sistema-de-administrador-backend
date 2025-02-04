package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "listGroup")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  //
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_company")
    private Company company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name =  "group_grants",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "grant_id")
    )
    private Set<Grant> grant;

    @ManyToMany
    @JoinTable(
            name =  "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> user;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "grupo_rol",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();*/

}
