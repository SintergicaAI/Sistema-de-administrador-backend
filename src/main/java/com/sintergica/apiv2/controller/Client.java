package com.sintergica.apiv2.controller;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.CompanyRepository;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class Client {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final UserRepository dataUserRepository;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final InvitationRepository invitationRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<User>> getClients() {
    return ResponseEntity.ok(this.dataUserRepository.findAll());
  }

  @PostMapping("/register")
  public ResponseEntity<HashMap<String, Object>> register(@Valid @RequestBody User user, @RequestParam UUID signInToken) {
    HashMap<String, Object> response = new HashMap<>();
    Optional<Invitation> invitation = invitationRepository.findById(signInToken);

    if(invitation.isEmpty()){
      response.put("message", "Invalid SignIn Token");
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    HashMap<String, Object> validateToken = InvitationTokenUtils.validateToken(invitation.get(), user.getEmail());

    if(!(boolean) validateToken.get("isValid")){
      response.put("message",validateToken.get("message"));
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    user.setRol(rolRepository.findByName("GUEST"));
    user.setCompany(null);// We'll need to remove this later
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (this.dataUserRepository.findByEmail(user.getEmail()) == null) {
      this.dataUserRepository.save(user);
      String token = generateToken(user.getEmail());
      response.put("Exito", true);
      response.put("token", token);

      invitation.get().setActive(false);
      invitationRepository.save(invitation.get());

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    response.put("Exito", false);
    response.put("token", null);

    return ResponseEntity.badRequest().body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody User userRequest) {

    Map<String, Object> respuesta = new HashMap<>();

    User user = this.dataUserRepository.findByEmail(userRequest.getEmail());

    if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
      String token = this.generateToken(userRequest.getEmail());
      respuesta.put("mensaje", "Bienvenidos");
      respuesta.put("exitoso", true);
      respuesta.put("token", token);
      return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    respuesta.put("mensaje", "dataUserRepository incorrectas");
    respuesta.put("exitoso", false);

    return ResponseEntity.badRequest().body(respuesta);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/addClientToCompany")
  public ResponseEntity<HashMap<String, String>> addClientToCompany(
      @RequestParam String email, @RequestParam UUID targetCompany) {

    Optional<Company> company = companyRepository.findById(targetCompany);
    User user = userRepository.findByEmail(email);

    if (user == null || company.isEmpty()) {

      HashMap<String, String> map = new HashMap<>();
      map.put("mensaje", "Error: El email o compañia no existen");

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    if (user.getCompany() == null) {
      user.setCompany(company.get());
      userRepository.save(user);
      HashMap<String, String> map = new HashMap<>();
      map.put("mensaje", "Cliente guardado correctamente");
      return ResponseEntity.ok(map);
    }

    HashMap<String, String> map = new HashMap<>();
    map.put("mensaje", "El usuario ya pertenece a una compañia");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/changeRolClient")
  public ResponseEntity<HashMap<String, String>> changeRolClient(
      @RequestParam String email, @RequestParam String newRolUser) {

    User user = userRepository.findByEmail(email);
    Rol rol = rolRepository.findByName(newRolUser);

    if (user == null || rol == null) {
      HashMap<String, String> map = new HashMap<>();
      map.put("mensaje", "Error: El rol o el usuario no existe");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    user.setRol(rol);
    userRepository.save(user);

    HashMap<String, String> map = new HashMap<>();
    map.put("mensaje", "Rol actualizado correctamente");
    return ResponseEntity.ok(map);
  }

  private String generateToken(String email) {
    return TokenUtilidades.createToken(Jwts.claims().subject(email).build());
  }
}
