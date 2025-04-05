package com.sintergica.apiv2.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "michelle")
public class MichelleConfig {
  private String url;
  private String token;
}
