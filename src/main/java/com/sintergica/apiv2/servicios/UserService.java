package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.SearchUserDTO;
import com.sintergica.apiv2.entidades.*;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import java.util.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User registerUser(User user) {
    return this.userRepository.save(user);
  }

  public User login(User user) {

    User userFound = userRepository.findByEmail(user.getEmail());

    if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
      return null;
    }

    return userFound;
  }

  public Collection<User> loginEmailsAndActiveUsers(Collection<String> emails, Company company) {

    Collection<User> usersFound = userRepository.findByEmailInAndCompany(emails, company);

    if(usersFound.stream().anyMatch(user -> !user.isActive()) || usersFound.isEmpty()) {
      return null;
    };

    return usersFound;
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Page<User> findAllByCompanyAndIsActive(
      Company company, boolean isActive, String username, List<String> groups, Pageable pageable) {
    return this.userRepository.findAllByCompanyAndIsActive(
        company, isActive, username, groups, pageable);
  }

  public List<User> findAllByCompanyAndIsActiveNotPageable(
      Company company, boolean isActive, String username, List<String> groups) {
    return this.userRepository.findAllByCompanyAndIsActiveNotPageable(
        company, isActive, username, groups);
  }

  public List<User> findByNameAndCompany(String fullName, Company company) {
    return this.userRepository.findByNameAndCompany(fullName, company);
  }

  public Page<SearchUserDTO> getUsersByName(String name, Company company, Pageable pageable) {

    Page<User> userPage =
        userRepository.findByCompanyAndNameAndLastNameStartingWith(
            company, name, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    ArrayList<SearchUserDTO> searchUserDTOs = new ArrayList<>();

    for (User user : userPage) {
      searchUserDTOs.add(
          new SearchUserDTO(
              user.getEmail(), user.getName(), user.getRol(), user.getGroups().size()));
    }
    return new PageImpl<>(searchUserDTOs, pageable, userPage.getTotalElements());
  }

  public String generateSessionToken(String email) {
    return TokenUtils.createToken(
        Jwts.claims().subject(email).add(TokenUtils.SUFFIX, TokenUtils.SESSION_TOKEN).build());
  }

  public String generateRefreshToken(String email) {
    return TokenUtils.createRefreshToken(
        Jwts.claims().subject(email).add(TokenUtils.SUFFIX, TokenUtils.REFRESH_TOKEN).build());
  }

  public User save(User user) {
    return this.userRepository.save(user);
  }

  public Set<User> findByInIdsAndActiveAndCompanyList(
      Set<UUID> inIds, boolean isActive, Company company) {
    return this.userRepository.findByIdInAndIsActiveAndCompany(inIds, isActive, company);
  }

  public User getUserLogged() {
    return this.userRepository.findByEmail(
        SecurityContextHolder.getContext().getAuthentication().getName());
  }

  public boolean canChangeRole(int weightUserLogged, int weightUserTarget) {
    return weightUserLogged <= weightUserTarget;
  }

  public User changeRol(User userTarget, Rol newRol) {
    userTarget.setRol(newRol);
    return this.userRepository.save(userTarget);
  }
}
