package com.sintergica.apiv2.entidades;

import com.sintergica.apiv2.entidades.utils.UserSubscriptionKeys;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Data;

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
