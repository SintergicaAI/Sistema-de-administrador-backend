package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public Page<User> findAllByCompany(Company company, Pageable pageable) {
    return this.userRepository.findAllByCompany(company, pageable);
  }

  private void generateNewUser(User user) {
    user.setRol(null);
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

  public String generateToken(String email) {
    return TokenUtils.createToken(Jwts.claims().subject(email).build());
  }

  public User save(User user) {
    return this.userRepository.save(user);
  }
}
