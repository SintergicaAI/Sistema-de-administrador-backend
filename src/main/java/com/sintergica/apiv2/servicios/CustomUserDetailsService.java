package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(correo);

    if (user == null) {
      throw new UsernameNotFoundException("Usuario no encontrado");
    }

    ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRol().getName()));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), authorities);
  }
}
