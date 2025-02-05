package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Package {

    @Id @GeneratedValue(strategy = GenerationType.AUTO) private UUID id;
    @Column private String nombre;
    @Column private double price;
    @Column private int users;
    @Column private String querys;
    @Column private String tokens;
    @Column private String knowledge;
    @Column private String models;
    @Column private String assistants;

}
