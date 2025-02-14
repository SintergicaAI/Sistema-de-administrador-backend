package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.*;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;

  public CompanyService(
      CompanyRepository companyRepository,
      UserRepository userRepository,
      GroupRepository groupRepository) {
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
  }

  public List<Company> findAll() {
    return this.companyRepository.findAll();
  }

  public Map<String, Boolean> add(Company company) {
    Optional<Company> optionalCompany = this.companyRepository.findById(company.getId());

    HashMap<String, Boolean> response = new HashMap<>();

    if (optionalCompany.isPresent()) {
      response.put("error", false);
      return response;
    }

    response.put("success", true);
    this.companyRepository.save(company);
    return response;
  }

  public Map<String, Company> getCompanyByUUID(UUID uuid) {
    HashMap<String, Company> response = new HashMap<>();

    Optional<Company> companyOptional = companyRepository.findById(uuid);

    if (companyOptional.isPresent()) {
      response.put("success", companyOptional.get());
      return response;
    }

    response.put("error", null);

    return response;
  }

  public Map<String, String> deleteUserToCompany(UUID uuid, String emailClient) {
    Map<String, String> response = new HashMap<>();

    Company company = this.companyRepository.findById(uuid).get();
    User user = this.userRepository.findByEmail(emailClient);

    if (user == null || company == null) {
      response.put("error", "el usuario o compañia no existen");
      return response;
    }

    if (!user.getCompany().equals(company)) {
      response.put("error", "el usuario no esta asociado a esta organizacion");
      return response;
    }

    List<Group> listGroupAssociate =
        this.groupRepository.findByCompanyAndUserContaining(company, user);

    for (Group group : listGroupAssociate) {
      group.getUser().remove(user);
      this.groupRepository.save(group);
    }

    user.setCompany(null);
    this.userRepository.save(user);

    response.put("success", "usuario eliminado de la organizacion");

    return response;
  }
}
