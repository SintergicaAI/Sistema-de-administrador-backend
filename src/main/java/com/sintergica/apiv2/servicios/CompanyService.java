package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.GroupDTO;
import com.sintergica.apiv2.dto.UserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
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
    return this.companyRepository.findById(uuid).get();
  }

  @Transactional
  public User addUserToCompany(User user, Company company) {
    user.setCompany(company);
    return this.userService.save(user);
  }

  @Transactional
  public User deleteUserFromCompany(User userFound) {
    userFound.setActive(false);
    return this.userService.save(userFound);
  }

  public Page<UserDTO> getGroupsCompany(Company company, Pageable pageable) {

    Page<User> users = this.userService.findAllByCompanyAndIsActive(company, true, pageable);

    List<UserDTO> data = new ArrayList<>();

    for (User userGroup : users) {

      List<GroupDTO> groupDTOList = new ArrayList<>();

      for (Group group : userGroup.getGroups()) {
        groupDTOList.add(new GroupDTO(group.getId(), group.getName()));
      }

      UserDTO list =
          new UserDTO(
              userGroup.getId(),
              userGroup.getName(),
              userGroup.getLastName(),
              userGroup.getEmail(),
              groupDTOList);

      data.add(list);
    }

    return new PageImpl<>(data, pageable, users.getTotalElements());
  }

  public Optional<Company> findById(UUID uuid) {
    return this.companyRepository.findById(uuid);
  }
}
