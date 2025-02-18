package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;

  public List<Company> findAll() {
    return this.companyRepository.findAll();
  }

  public void add(Company company) {
    this.companyRepository.save(company);
  }

  public Company getCompanyById(UUID uuid) {

    Optional<Company> companyOptional = this.companyRepository.findById(uuid);

    if(!companyOptional.isPresent()) {
      throw new CompanyNotFound("Compañia no encontrada");
    }

    return this.companyRepository.findById(uuid).get();


  }
}
