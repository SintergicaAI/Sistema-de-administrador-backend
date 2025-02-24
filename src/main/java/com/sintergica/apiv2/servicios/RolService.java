package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.RolRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Data
public class RolService {
  private final RolRepository rolRepository;
  private final UserService userService;

  private final List<Rol> roles = new ArrayList<>();

  @EventListener(ApplicationReadyEvent.class)
  protected void loadRoles() {
    roles.addAll(rolRepository.findAll());
  }

  public Rol getRolByName(String name) {
    for (Rol role : roles) {
      if (name.equals(role.getName())) {
        return role;
      }
    }
    return null;
  }

  @Transactional
  public User changeUserRole(User user, Rol role) {
    user.setRol(role);
    user = userService.save(user);
    return user;
  }

  public Rol save(Rol role) {
    return this.rolRepository.save(role);
  }
}
