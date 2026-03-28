package com.odonto.api.auth.security;

import com.odonto.api.auth.repository.RevokedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    public JwtAuthFilter(JwtService jwtService,
                         UserDetailsService userDetailsService,
                         RevokedTokenRepository revokedTokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String header = request.getHeader("Authorization");
        log.debug(">>> JwtAuthFilter - URI: {} | Header: {}", uri, header);

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("<<< Sem Bearer token, passando adiante");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.isTokenValid(token)) {
            log.warn("<<< Token inválido para URI: {}", uri);
            chain.doFilter(request, response);
            return;
        }

        String jti = jwtService.extractJti(token);
        if (revokedTokenRepository.existsByJti(jti)) {
            log.warn("<<< Token revogado (jti: {}) para URI: {}", jti, uri);
            chain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token);
        log.debug("Token válido para email: {} | URI: {}", email, uri);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("<<< Autenticação definida com sucesso para: {}", email);

        chain.doFilter(request, response);
    }
}
