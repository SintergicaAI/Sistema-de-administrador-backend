package com.sintergica.apiv2.entidades;

import com.sintergica.apiv2.entidades.utils.CompanySubscriptionKeys;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@IdClass(CompanySubscriptionKeys.class)
public class CompanySubscription {

  @Id
  @ManyToOne
  @JoinColumn(name = "packageId", referencedColumnName = "id") // "id" es el nombre en Package
  private Package packageId;

  @Id
  @ManyToOne
  @JoinColumn(name = "subscriptionId", referencedColumnName = "id") // "id" es el nombre en User
  private Company subscriptionId;

  @Column(name = "startDate")
  private Date startDate;
}
