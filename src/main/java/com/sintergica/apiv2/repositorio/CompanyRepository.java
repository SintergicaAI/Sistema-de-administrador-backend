package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Company findByName(String name);

}
