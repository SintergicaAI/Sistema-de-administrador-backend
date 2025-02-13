package com.sintergica.apiv2.interceptors;

import com.sintergica.apiv2.servicios.CustomUserDetailsService;
import com.sintergica.apiv2.utilidades.TokenUtilidades;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTFiltro extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;
  private static final Logger logger = LoggerFactory.getLogger(JWTFiltro.class);

  public JWTFiltro(CustomUserDetailsService customUserDetailsService) {

    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (TokenUtilidades.getTokenClaims(token) != null) {
        String correo = TokenUtilidades.getTokenClaims(token).getSubject();

        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails user = customUserDetailsService.loadUserByUsername(correo);

          if (!TokenUtilidades.isExpired(token)) {

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
