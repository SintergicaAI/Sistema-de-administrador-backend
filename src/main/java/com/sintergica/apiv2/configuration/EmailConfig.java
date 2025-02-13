package com.sintergica.apiv2.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
    private boolean enable_auth;
    private boolean enable_tls;
    private int smtp_port;
    private int ssl_port;
    private String server;
    private String from_email;
    private String email_password;
    private String base_url;
}
