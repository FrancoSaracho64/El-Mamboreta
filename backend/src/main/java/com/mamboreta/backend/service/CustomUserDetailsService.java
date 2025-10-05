package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Usuario;
import com.mamboreta.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                .collect(Collectors.toSet());

        // El constructor User() espera (username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getActivo(),   // enabled
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                true,                  // accountNonLocked
                authorities
        );
    }
}
