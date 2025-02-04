package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.entidades.Group;
import com.sintergica.apiv2.repositorio.RolRepository;
import com.sintergica.apiv2.repositorio.UserRepository;
import com.sintergica.apiv2.repositorio.GroupRepository;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class Clientes {

    @Autowired
    private UserRepository credenciales;

    @Autowired
    private GroupRepository grupoRepositorio;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //409
    //400 o  406
    @PostMapping("/register")
    public ResponseEntity<HashMap<String,String>> registrar(@Valid @RequestBody User user) {
        HashMap<String, String> map = new HashMap<>();
        user.setRol(rolRepository.findByName("GUEST"));
        user.setCompany(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.credenciales.save(user);
        map.put("Exito","Registrado correctamente");
        return ResponseEntity
                .ok(map);
    }


    @PostMapping("/login")
    public Map<String, Object> acceder(@Valid @RequestBody User userRequest) {

        Map<String, Object> respuesta = new HashMap<>();

        User user = this.credenciales.findByEmail(userRequest.getEmail());

        if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {

            String token = TokenUtilidades.createToken(
                    Jwts.claims()
                            .subject(userRequest.getEmail())
                            .build());

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
    public ResponseEntity<String> actualizarGrupoCliente(@RequestParam String correoClienteObjetivo, @RequestParam String nuevoGrupo) {

        Group group = grupoRepositorio.findByName(nuevoGrupo);
        User User = credenciales.findByEmail(correoClienteObjetivo);

        if((User == null) || (group == null)) {
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
        String correo = usuarioEnContexto.getName().toString();

        return this.credenciales.findByEmail(correo);
    }

}
