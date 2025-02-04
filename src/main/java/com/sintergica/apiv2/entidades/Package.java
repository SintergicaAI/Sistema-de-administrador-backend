package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Package {

    @Id @GeneratedValue(strategy = GenerationType.AUTO) private UUID id;
    @Column private double price;
    @Column private int user;



}
