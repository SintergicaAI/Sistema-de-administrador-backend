package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name") private String name;
    @Column(name = "RFC") private String RFC;
    @Column(name = "address") private String address;

}
