package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

  Company findByName(String name);
}
