package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.EntidadClientes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
//@CrossOrigin(origins = "*")
public class Clientes {

    @Autowired
    private RepositorioClientes credenciales;

    @PostMapping("/registrar")
    public void registrar(@Valid @RequestBody EntidadClientes cliente){
        this.credenciales.save(cliente);
    }

    @PostMapping("/acceder")
    public String acceder(@Valid @RequestBody EntidadClientes cliente){

        boolean clienteValido = this.credenciales.existsByCorreoAndContrasena(
                cliente.getCorreo(),
                cliente.getContrasena()
        );

        return clienteValido ? "Bienvenidos" : "Credenciales incorrectas";
    }

    @GetMapping("/obtenerTodosLosUsuarios")
    public List<EntidadClientes> listar() {
        return this.credenciales.findAll();
    }

}
