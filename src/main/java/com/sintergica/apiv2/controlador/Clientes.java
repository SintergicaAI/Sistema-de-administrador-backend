package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.EntidadClientes;
import com.sintergica.apiv2.repositorio.RepositorioClientes;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
            String token = TokenUtilidades.createToken(Jwts.claims().subject(cliente.getCorreo())
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

    @GetMapping("/listar")
    public List<EntidadClientes> listar(@RequestHeader("Authorization") String token) {

        token = token.replace("Bearer ", "");

        System.out.println(TokenUtilidades.getTokenClaims(token));

        return this.credenciales.findAll();
    }

    @GetMapping("/listarPorId")
    public EntidadClientes listarPorId(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        String x = TokenUtilidades.getTokenClaims(token).getSubject();
        return this.credenciales.findByCorreo(x);
    }

}
