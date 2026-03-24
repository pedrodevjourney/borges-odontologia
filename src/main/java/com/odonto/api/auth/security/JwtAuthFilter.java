package com.odonto.api.auth.security;

import com.odonto.api.auth.repository.RevokedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.isTokenValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        String jti = jwtService.extractJti(token);
        if (revokedTokenRepository.existsByJti(jti)) {
            chain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
}
