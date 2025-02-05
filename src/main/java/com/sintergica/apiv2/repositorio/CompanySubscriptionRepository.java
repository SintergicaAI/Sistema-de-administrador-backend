package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.CompanySubscription;
import com.sintergica.apiv2.entidades.utils.CompanySubscriptionKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, CompanySubscriptionKeys> {
}
