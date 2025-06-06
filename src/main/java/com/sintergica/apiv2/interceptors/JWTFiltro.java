package com.sintergica.apiv2.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sintergica.apiv2.exceptions.globals.Warnings;
import com.sintergica.apiv2.servicios.CustomUserDetailsService;
import com.sintergica.apiv2.utilidades.PublicEndpoints;
import com.sintergica.apiv2.utilidades.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTFiltro extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;

  public JWTFiltro(CustomUserDetailsService customUserDetailsService) {

    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    boolean isNotPublicEndpoint =
        Arrays.stream(PublicEndpoints.publicEndpoints)
            .noneMatch(endpoint -> request.getRequestURI().equals(endpoint));

    if (isNotPublicEndpoint && authHeader == null) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json");

      ObjectMapper objectMapper = new ObjectMapper();
      response
          .getWriter()
          .write(objectMapper.writeValueAsString(new Warnings("Send a token", new Date())));
      return;
    }

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (TokenUtils.getTokenClaims(token) == null) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        response
            .getWriter()
            .write(objectMapper.writeValueAsString(new Warnings("invalid token", new Date())));
        return;
      }

      if (TokenUtils.getTokenClaims(token) != null) {
        String correo = TokenUtils.getTokenClaims(token).getSubject();
        String typeToken = TokenUtils.getTypeToken(token);

        if (typeToken != null && typeToken.equals(TokenUtils.SESSION_TOKEN)) {
          if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = customUserDetailsService.loadUserByUsername(correo);

            if (!TokenUtils.isExpired(token)) {

              UsernamePasswordAuthenticationToken authToken =
                  new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authToken);
            }
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
