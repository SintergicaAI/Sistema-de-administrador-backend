package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Company;
import com.sintergica.apiv2.repositorio.CompanySubscriptionRepository;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
