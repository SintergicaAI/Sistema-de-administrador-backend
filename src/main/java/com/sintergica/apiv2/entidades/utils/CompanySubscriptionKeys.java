package com.sintergica.apiv2.entidades.utils;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CompanySubscriptionKeys implements Serializable {
  private UUID packageId;
  private UUID subscriptionId;
}
