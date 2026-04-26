package com.pogbe.bankingsystem.security;

import com.pogbe.bankingsystem.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilters extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilters(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtils.getValidatedAccessClaims(token);
                String tokenType = claims.get("tokenType", String.class);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                if (!"ACCESS".equals(tokenType) || username == null || role == null) {
                    throw new IllegalArgumentException("Invalid access token claims");
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                authenticationToken.setDetails(claims);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
