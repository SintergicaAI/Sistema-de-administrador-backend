package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.entidades.CompanySubscription;
import com.sintergica.apiv2.entidades.utils.CompanySubscriptionKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanySubscriptionRepository
    extends JpaRepository<CompanySubscription, CompanySubscriptionKeys> {
    List<CompanySubscription> findAllBySubscriptionId_Id(UUID subscriptionIdId);
    void deleteSubscription(UUID subscriptionId);
}
