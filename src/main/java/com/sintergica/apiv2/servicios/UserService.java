package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.SearchUserDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.*;
import java.util.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
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
    this.generateNewUser(user);
    return this.userRepository.save(user);
  }

  public User login(User user) {

    User userFound = userRepository.findByEmail(user.getEmail());

    if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
      return null;
    }

    return userFound;
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Page<User> findAllByCompanyAndIsActive(
      Company company, boolean isActive, Pageable pageable) {
    return this.userRepository.findAllByCompanyAndIsActive(company, isActive, pageable);
  }

  public Page<SearchUserDTO> getUsersByName(String name, Company company, Pageable pageable) {

    Page<User> userPage =
        userRepository.findByCompanyAndNameStartingWith(
            company, name, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    ArrayList<SearchUserDTO> searchUserDTOs = new ArrayList<>();

    for (User user : userPage) {
      searchUserDTOs.add(
          new SearchUserDTO(
              user.getEmail(), user.getName(), user.getRol(), user.getGroups().size()));
    }

    return new PageImpl<>(searchUserDTOs, pageable, userPage.getTotalElements());
  }

  private void generateNewUser(User user) {
    user.setRol(null);
    user.setCompany(null);
    user.setActive(true);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
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

  public User getUserLogged() {
    return this.userRepository.findByEmail(
        SecurityContextHolder.getContext().getAuthentication().getName());
  }

  public boolean hasLoggedInUserTheRole(String role) {
    Collection<? extends GrantedAuthority> authentication =
        SecurityContextHolder.getContext().getAuthentication().getAuthorities();

    return authentication.stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
  }
}
