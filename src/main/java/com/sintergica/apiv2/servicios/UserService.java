package com.sintergica.apiv2.servicios;
import com.sintergica.apiv2.entidades.EntidadClientes;
import com.sintergica.apiv2.repositorio.RepositorioClientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private RepositorioClientes userRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        EntidadClientes cliente = userRepository.findByCorreo(correo);

        if (cliente == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        return new org.springframework.security.core.userdetails.User(
                cliente.getCorreo(),
                cliente.getContrasena(),
                new ArrayList<>()
        );
    }

    public EntidadClientes findByCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }
}
