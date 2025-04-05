package com.sintergica.apiv2.interceptors;

import com.sintergica.apiv2.servicios.InvalidatedTokensService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class BannedTokenFilter implements Filter {
  private final InvalidatedTokensService invalidTokenService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String authHeader = httpRequest.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = httpRequest.getHeader("Authorization").split(" ")[1];
      if (invalidTokenService.isTokenBanned(token)) {
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
      }
    }
    chain.doFilter(request, response);
  }
}
