package com.sintergica.apiv2.repositorio;

import com.sintergica.apiv2.entidades.UserSubscription;
import com.sintergica.apiv2.entidades.utils.UserSubscriptionKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository
    extends JpaRepository<UserSubscription, UserSubscriptionKeys> {}
