package com.sintergica.apiv2.seguridad;

import com.sintergica.apiv2.interceptors.JWTFiltro;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JWTFiltro jwtFiltro;

  public SecurityConfig(JWTFiltro jwtFiltro) {
    this.jwtFiltro = jwtFiltro;
  }

  @Bean
  SecurityFilterChain configure(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorizeRequests -> {
              authorizeRequests
                  .requestMatchers("/clients/login")
                  .permitAll()
                  .requestMatchers("/clients/register")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .sessionManagement(
            httpSecuritySessionManagementConfigurer -> {
              httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                  SessionCreationPolicy.STATELESS);
            })
        .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
