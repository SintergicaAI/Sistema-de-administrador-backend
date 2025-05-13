package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.repositorio.CompanySubscriptionRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class CompanySubscriptionService {
    private final CompanySubscriptionRepository companySubscriptionRepository;

    public void deleteSubscription(UUID subscriptionId) {
        Company company = new Company();

        company.setId(subscriptionId);
        this.companySubscriptionRepository.deleteCompanySubscriptionBySubscriptionId(company);
    }
}
