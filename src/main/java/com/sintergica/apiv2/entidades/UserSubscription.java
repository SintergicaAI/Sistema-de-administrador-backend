package com.sintergica.apiv2.entidades;


import com.sintergica.apiv2.entidades.utils.CompanySubscriptionKeys;
import com.sintergica.apiv2.entidades.utils.UserSubscriptionKeys;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@IdClass(UserSubscriptionKeys.class)
public class UserSubscription {

    @Id
    @ManyToOne
    @JoinColumn(name = "packageId", referencedColumnName = "id") // "id" es el nombre en Package
    private Package packageId;

    @Id
    @ManyToOne
    @JoinColumn(name = "subscriptionId", referencedColumnName = "id") // "id" es el nombre en User
    private User subscriptionId;

    @Column(name = "startDate")
    private Date startDate;


}
