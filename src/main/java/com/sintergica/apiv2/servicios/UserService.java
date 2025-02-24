package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.dto.LoginAndRegisterDTO;
import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.exceptions.user.PasswordConflict;
import com.sintergica.apiv2.exceptions.user.UserConflict;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtils;
import io.jsonwebtoken.Jwts;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public LoginAndRegisterDTO registerUser(User user) {

    Optional.ofNullable(this.userRepository.findByEmail(user.getEmail()))
        .ifPresent(
            user1 -> {
              throw new UserConflict("Este email ya existe en el sistema");
            });

    this.generateNewUser(user);
    User savedUser = this.userRepository.save(user);

    return new LoginAndRegisterDTO(
        user.getEmail(),
        user.getName(),
        user.getLastName(),
        this.generateToken(savedUser.getEmail()));
  }

  public LoginAndRegisterDTO login(User user) {

    User userFound =
        Optional.ofNullable(this.userRepository.findByEmail(user.getEmail()))
            .orElseThrow(() -> new UserNotFound("Email no encontrado"));

    if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
      throw new PasswordConflict("Contraseña incorrecta");
    }

    return new LoginAndRegisterDTO(
        userFound.getEmail(),
        userFound.getName(),
        userFound.getLastName(),
        this.generateToken(userFound.getEmail()));
  }

  public User findByEmail(String email) {

    if (userRepository.findByEmail(email) == null) {
      throw new UserNotFound("Usuario no encontrado");
    }

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
