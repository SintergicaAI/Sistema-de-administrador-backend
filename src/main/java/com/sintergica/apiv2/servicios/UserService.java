package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.AuthEntity;
import com.sintergica.apiv2.repositorio.AuthRepository;
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
public class UserService implements UserDetailsService {
  private final AuthRepository authRepository;
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
    AuthEntity user = authRepository.findByEmail(correo);

    if (user == null) {
      throw new UsernameNotFoundException("Usuario no encontrado");
    }

    ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

    /*user.getUserGroups().forEach(group -> {
        group.getGrant().forEach(grant -> {
            authorities.add(new SimpleGrantedAuthority(grant.getName()));
        });
    });*/

    authorities.add(new SimpleGrantedAuthority("ROLE_" + userRepository.findByEmail(correo).getRole()));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), authorities);
  }

  public AuthEntity findByEmail(String correo) {
    return authRepository.findByEmail(correo);
  }
}
