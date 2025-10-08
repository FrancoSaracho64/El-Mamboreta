package mamboreta.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") || path.startsWith("/h2-console");
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token inválido
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // Extraer roles del token de forma robusta
                    var claims = jwtUtil.extractAllClaims(jwt);
                    Object rolesObj = claims.get("roles");

                    java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();

                    if (rolesObj instanceof java.util.Collection<?>) {
                        for (Object role : (java.util.Collection<?>) rolesObj) {
                            if (role != null) {
                                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
                            }
                        }
                    } else if (rolesObj instanceof String) {
                        // roles stored as comma separated string
                        String rolesStr = (String) rolesObj;
                        for (String r : rolesStr.split(",")) {
                            if (!r.isBlank()) authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.trim()));
                        }
                    }

                    // Si no hay roles en el token, usar los del userDetails como fallback
                    if (authorities.isEmpty()) {
                        userDetails.getAuthorities().forEach(a -> authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(a.getAuthority())));
                    }


                    // Logs diagnósticos simples
                    logger.info("[JWT Filter] user= {} rolesFromToken= {} authorities= {}", username, rolesObj, authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("[JWT Filter] token inválido o expirado para user= {}", username);
                }
            } catch (Exception e) {
                logger.error("[JWT Filter] error procesando token", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
