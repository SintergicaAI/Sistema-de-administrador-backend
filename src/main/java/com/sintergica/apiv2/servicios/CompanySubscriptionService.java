package com.sintergica.apiv2.servicios;

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

    void deleteSubscription(UUID subscriptionId) {
        this.companySubscriptionRepository.deleteSubscription(subscriptionId);
    }
}
