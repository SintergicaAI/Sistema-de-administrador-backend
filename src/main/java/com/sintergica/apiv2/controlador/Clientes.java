package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.EntidadClientes;
import com.sintergica.apiv2.repositorio.RepositorioClientes;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
//@CrossOrigin(origins = "*")
public class Clientes {

    @Autowired
    private RepositorioClientes credenciales;

    @PostMapping("/register")
    public void registrar(@Valid @RequestBody EntidadClientes cliente){
        cliente.setRol("USER");
        this.credenciales.save(cliente);
    }

    @PostMapping("/login")
    public Map<String, Object> acceder(@Valid @RequestBody EntidadClientes cliente) {
        Map<String, Object> respuesta = new HashMap<>();

        boolean clienteValido = this.credenciales.existsByCorreoAndContrasena(
                cliente.getCorreo(),
                cliente.getContrasena()
        );

        if (clienteValido) {
            String token = TokenUtilidades.createToken(
                    Jwts.claims()
                            .subject(cliente.getCorreo())
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar")
    public List<EntidadClientes> listar() {

        return this.credenciales.findAll();
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public EntidadClientes listarPorId() {

        Authentication usuarioEnContexto = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(usuarioEnContexto.getAuthorities().toString());
        String correo = usuarioEnContexto.getName().toString();

        return this.credenciales.findByCorreo(correo);
    }

}
