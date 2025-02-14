package com.sintergica.apiv2.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "configured.messages")
public class MessagesConfig {
	private Map<String, String> messages;
}
