package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {}
