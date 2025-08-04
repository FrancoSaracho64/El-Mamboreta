package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.RedSocial;
import com.mamboreta.backend.repository.RedSocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RedSocialService {

    @Autowired
    private RedSocialRepository redSocialRepository;

    /**
     * Obtiene todas las redes sociales activas
     */
    public List<RedSocial> findAllActivos() {
        return redSocialRepository.findByActivoTrue();
    }

    /**
     * Obtiene todas las redes sociales
     */
    public List<RedSocial> findAll() {
        return redSocialRepository.findAll();
    }

    /**
     * Busca una red social por ID
     */
    public Optional<RedSocial> findById(Long id) {
        return redSocialRepository.findById(id);
    }

    /**
     * Busca redes sociales por nombre de red
     */
    public List<RedSocial> findByRed(String red) {
        return redSocialRepository.findByRed(red);
    }

    /**
     * Busca redes sociales por usuario
     */
    public List<RedSocial> findByUsuarioContaining(String usuario) {
        return redSocialRepository.findByUsuarioContaining(usuario);
    }

    /**
     * Guarda una nueva red social
     */
    public RedSocial save(RedSocial redSocial) {
        // Validar que la URL sea válida si se proporciona
        if (redSocial.getUrl() != null && !redSocial.getUrl().isEmpty()) {
            if (!isValidUrl(redSocial.getUrl())) {
                throw new RuntimeException("La URL de la red social no es válida");
            }
        }

        return redSocialRepository.save(redSocial);
    }

    /**
     * Actualiza una red social existente
     */
    public RedSocial update(Long id, RedSocial redSocial) {
        Optional<RedSocial> redSocialExistente = redSocialRepository.findById(id);
        if (redSocialExistente.isPresent()) {
            // Validar que la URL sea válida si se proporciona
            if (redSocial.getUrl() != null && !redSocial.getUrl().isEmpty()) {
                if (!isValidUrl(redSocial.getUrl())) {
                    throw new RuntimeException("La URL de la red social no es válida");
                }
            }

            redSocial.setId(id);
            return redSocialRepository.save(redSocial);
        } else {
            throw new RuntimeException("Red social no encontrada con ID: " + id);
        }
    }

    /**
     * Desactiva una red social (borrado lógico)
     */
    public void deleteById(Long id) {
        Optional<RedSocial> redSocial = redSocialRepository.findById(id);
        if (redSocial.isPresent()) {
            RedSocial redSocialActual = redSocial.get();
            redSocialActual.setActivo(false);
            redSocialRepository.save(redSocialActual);
        } else {
            throw new RuntimeException("Red social no encontrada con ID: " + id);
        }
    }

    /**
     * Valida el formato de la URL
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Patrón básico para URLs
        String urlPattern = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$";
        return url.matches(urlPattern);
    }

    /**
     * Busca redes sociales por tipo de red y usuario
     */
    public List<RedSocial> findByRedAndUsuarioContaining(String red, String usuario) {
        return redSocialRepository.findByRedAndUsuarioContaining(red, usuario);
    }
} 