package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final InvalidatedTokensService invalidTokensService;
  private final UserRepository userRepository;
  private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

  @Override
  public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
    logger.info("Loading user by username: " + correo);
    User user = userRepository.findByEmail(correo);

    if (user == null) {
      throw new UsernameNotFoundException("Usuario no encontrado");
    }

    ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

    if (user.getRol() != null) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRol().getName()));
    }
    logger.info("LOAD user by username: " + correo);
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), authorities);
  }
}
