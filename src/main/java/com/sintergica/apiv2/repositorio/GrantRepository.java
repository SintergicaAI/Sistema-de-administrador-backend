package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Grant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantRepository extends CrudRepository<Grant, Long> {

    Grant findByName(String name);

}
