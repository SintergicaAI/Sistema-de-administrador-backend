package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Rol;
import com.sintergica.apiv2.repositorio.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RolService {
	private final RolRepository repository;
	private final List<Rol> roles = new ArrayList<>();

	@EventListener(ApplicationReadyEvent.class)
	private void loadRoles() {
		roles.addAll(repository.findAll());
	}

	public Rol getRolByName(String name) {
		return roles.stream()
			.filter(rol -> rol.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
	}
}
