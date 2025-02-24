package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final UserService userService;

  public List<Company> findAll() {
    return this.companyRepository.findAll();
  }

  public Company add(Company company) {
    return this.companyRepository.save(company);
  }

  public Company getCompanyById(UUID uuid) {

    Optional<Company> companyOptional = this.companyRepository.findById(uuid);

    if (!companyOptional.isPresent()) {
      throw new CompanyNotFound("Compañia no encontrada");
    }

    return this.companyRepository.findById(uuid).get();
  }

  @Transactional
  public User addUserToCompany(String email, UUID targetCompanyId) {

    User user = this.userService.findByEmail(email);
    Optional<Company> company = this.companyRepository.findById(targetCompanyId);

    company.orElseThrow(
        () -> {
          throw new CompanyNotFound("Compañia no encontrada");
        });

    if (user.getCompany() != null) {
      throw new CompanyUserConflict("El usuario ya tiene asociada una compañia");
    }

    user.setCompany(company.get());

    return this.userService.save(user);
  }

  public Page<UserDTO> getGroupsCompany(Pageable pageable) {

    String userName = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = this.userService.findByEmail(userName);
    Company company = user.getCompany();

    Optional.ofNullable(company)
        .orElseThrow(
            () -> {
              throw new CompanyNotFound("El usuario no tiene una compañia asociada");
            });

    Page<User> users = this.userService.findAllByCompany(company, pageable);

    List<UserDTO> userDTOs = new ArrayList<>();

    for (User userGroup : users) {

      List<GroupDTO> groupDTOList = new ArrayList<>();

      for (Group group : userGroup.getGroups()) {
        groupDTOList.add(new GroupDTO(group.getId(), group.getName()));
      }

      UserDTO userDTO =
          new UserDTO(
              userGroup.getId(),
              userGroup.getName(),
              userGroup.getLastName(),
              userGroup.getEmail(),
              groupDTOList);

      userDTOs.add(userDTO);
    }

    return new PageImpl<>(userDTOs, pageable, users.getTotalElements());
  }
}
