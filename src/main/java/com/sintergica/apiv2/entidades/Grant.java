package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "list_grants")
@Data
public class Grant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name") private String name;

    @Column(name = "description") private String description;

}
