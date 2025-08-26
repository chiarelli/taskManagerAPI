package com.github.chiarelli.taskmanager.presentation.security;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.chiarelli.taskmanager.application.dtos.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final String SECRET_KEY;

  public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
    this.SECRET_KEY = secret;
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = extractTokenFromRequest(request);

    try {
      if (token == null) throw new Exception("Token ausente");

      Claims claims = tokenClaims(token);

      UserDTO userLogged = new UserDTO(
          UUID.fromString(claims.getSubject()), // sub
          claims.get("username", String.class), // username
          claims.get("role", String.class)      // role
      );

      // Converter UserDTO em Authentication
      var auth = new UsernamePasswordAuthenticationToken(
          userLogged,
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + userLogged.getRole()))
      );

      SecurityContextHolder.getContext().setAuthentication(auth);

      filterChain.doFilter(request, response);

    } catch (Exception e) {
      SecurityContextHolder.clearContext();

      // Retorna 401 diretamente
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado");
    }    
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public Claims tokenClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

}
