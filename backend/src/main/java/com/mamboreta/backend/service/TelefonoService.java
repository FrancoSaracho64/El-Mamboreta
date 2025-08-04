package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Telefono;
import com.mamboreta.backend.repository.TelefonoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TelefonoService {

    @Autowired
    private TelefonoRepository telefonoRepository;

    /**
     * Obtiene todos los teléfonos activos
     */
    public List<Telefono> findAllActivos() {
        return telefonoRepository.findByActivoTrue();
    }

    /**
     * Obtiene todos los teléfonos
     */
    public List<Telefono> findAll() {
        return telefonoRepository.findAll();
    }

    /**
     * Busca un teléfono por ID
     */
    public Optional<Telefono> findById(Long id) {
        return telefonoRepository.findById(id);
    }

    /**
     * Busca teléfonos por número
     */
    public List<Telefono> findByNumeroContaining(String numero) {
        return telefonoRepository.findByNumeroContaining(numero);
    }

    /**
     * Busca teléfonos por tipo
     */
    public List<Telefono> findByTipo(String tipo) {
        return telefonoRepository.findByTipo(tipo);
    }

    /**
     * Guarda un nuevo teléfono
     */
    public Telefono save(Telefono telefono) {
        // Validar formato del número de teléfono
        if (!isValidPhoneNumber(telefono.getNumero())) {
            throw new RuntimeException("El formato del número de teléfono no es válido");
        }

        return telefonoRepository.save(telefono);
    }

    /**
     * Actualiza un teléfono existente
     */
    public Telefono update(Long id, Telefono telefono) {
        Optional<Telefono> telefonoExistente = telefonoRepository.findById(id);
        if (telefonoExistente.isPresent()) {
            // Validar formato del número de teléfono
            if (!isValidPhoneNumber(telefono.getNumero())) {
                throw new RuntimeException("El formato del número de teléfono no es válido");
            }

            telefono.setId(id);
            return telefonoRepository.save(telefono);
        } else {
            throw new RuntimeException("Teléfono no encontrado con ID: " + id);
        }
    }

    /**
     * Desactiva un teléfono (borrado lógico)
     */
    public void deleteById(Long id) {
        Optional<Telefono> telefono = telefonoRepository.findById(id);
        if (telefono.isPresent()) {
            Telefono telefonoActual = telefono.get();
            telefonoActual.setActivo(false);
            telefonoRepository.save(telefonoActual);
        } else {
            throw new RuntimeException("Teléfono no encontrado con ID: " + id);
        }
    }

    /**
     * Valida el formato del número de teléfono
     */
    private boolean isValidPhoneNumber(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        
        // Patrón básico para números de teléfono argentinos
        // Permite formatos como: +54 11 1234-5678, 011 1234-5678, 1234-5678, etc.
        String phonePattern = "^[+]?[0-9\\s\\-\\(\\)]+$";
        return numero.matches(phonePattern) && numero.replaceAll("[^0-9]", "").length() >= 7;
    }
} 