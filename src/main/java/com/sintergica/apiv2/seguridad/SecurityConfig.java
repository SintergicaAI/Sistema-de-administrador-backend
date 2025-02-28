package com.sintergica.apiv2.seguridad;

import com.fasterxml.jackson.databind.*;
import com.sintergica.apiv2.exceptions.globals.*;
import com.sintergica.apiv2.interceptors.JWTFiltro;
import jakarta.servlet.http.*;
import java.util.*;
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
                  .requestMatchers("/users/login")
                  .permitAll()
                  .requestMatchers("/users/register")
                  .permitAll()
                  .requestMatchers("/users/logout")
                  .permitAll()
                  .requestMatchers("/users/refreshToken")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .sessionManagement(
            httpSecuritySessionManagementConfigurer -> {
              httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                  SessionCreationPolicy.STATELESS);
            })
        .exceptionHandling(
            httpSecurityExceptionHandlingConfigurer ->
                httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(
                    (request, response, accessDeniedException) -> {
                      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                      response.setContentType("application/json");

                      ObjectMapper objectMapper = new ObjectMapper();
                      response
                          .getWriter()
                          .write(
                              objectMapper.writeValueAsString(
                                  new Warnings(
                                      "Falta de permisos para acceder a este recurso",
                                      new Date())));
                    }))
        .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
