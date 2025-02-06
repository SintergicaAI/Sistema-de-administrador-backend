package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clientes")
public class Clientes {
  private final UserRepository credenciales;
  private final GroupRepository grupoRepositorio;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;

  // 409
  // 400 o  406
  @PostMapping("/register")
  public ResponseEntity<HashMap<String, Object>> registrar(@Valid @RequestBody User user) {
    HashMap<String, Object> map = new HashMap<>();
    user.setRol(rolRepository.findByName("GUEST"));
    user.setCompany(null);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (this.credenciales.findByEmail(user.getEmail()) == null) {
      this.credenciales.save(user);
      String token = generateToken(user.getEmail());
      map.put("Exito", true);
      map.put("token", token);
    } else {
      map.put("Exito", false);
      map.put("Exito", "Registrado correctamente");
    }

    return ResponseEntity.ok(map);
  }

  @PostMapping("/login")
  public Map<String, Object> acceder(@Valid @RequestBody User userRequest) {

    Map<String, Object> respuesta = new HashMap<>();

    User user = this.credenciales.findByEmail(userRequest.getEmail());

    if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {

      String token = this.generateToken(userRequest.getEmail());

      respuesta.put("mensaje", "Bienvenidos");
      respuesta.put("exitoso", true);
      respuesta.put("token", token);

    } else {
      respuesta.put("mensaje", "Credenciales incorrectas");
      respuesta.put("exitoso", false);
    }

    return respuesta;
  }

  @PreAuthorize("hasAuthority('ALLOW FILE UPLOAD') or hasRole('ADMIN')")
  @PostMapping("/ejemplo")
  public String uploadFile() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/listar")
  public List<User> listar() {
    return this.credenciales.findAll();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/actualizargrupo")
  public ResponseEntity<String> actualizarGrupoCliente(
      @RequestParam String correoClienteObjetivo, @RequestParam String nuevoGrupo) {

    Group group = grupoRepositorio.findByName(nuevoGrupo);
    User User = credenciales.findByEmail(correoClienteObjetivo);

    if ((User == null) || (group == null)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Error: Cliente no encontrado o grupo " + " no encontrado.");
    }

    /*User.getGroups().clear();
    User.getGroups().add(group);*/

    credenciales.save(User);

    return ResponseEntity.ok("Actualizado");
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/profile")
  public User listarPorId() {
    Authentication usuarioEnContexto = SecurityContextHolder.getContext().getAuthentication();

    System.out.println(usuarioEnContexto.getAuthorities().toString());
    String correo = usuarioEnContexto.getName();

    return this.credenciales.findByEmail(correo);
  }

  private String generateToken(String email) {
    return TokenUtilidades.createToken(Jwts.claims().subject(email).build());
  }
}
