package com.sintergica.apiv2.interceptors;

import com.sintergica.apiv2.servicios.CustomUserDetailsService;
import com.sintergica.apiv2.utilidades.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

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
