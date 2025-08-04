package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Documento;
import com.mamboreta.backend.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    /**
     * Obtiene todos los documentos
     */
    public List<Documento> findAll() {
        return documentoRepository.findAll();
    }

    /**
     * Busca un documento por ID
     */
    public Optional<Documento> findById(Long id) {
        return documentoRepository.findById(id);
    }

    /**
     * Busca un documento por número
     */
    public Optional<Documento> findByNumero(String numero) {
        return documentoRepository.findByNumero(numero);
    }

    /**
     * Busca documentos por tipo
     */
    public List<Documento> findByTipo(String tipo) {
        return documentoRepository.findByTipo(tipo);
    }

    /**
     * Guarda un nuevo documento
     */
    public Documento save(Documento documento) {
        // Validar que el número de documento no exista
        if (documentoRepository.existsByNumero(documento.getNumero())) {
            throw new RuntimeException("Ya existe un documento con el número: " + documento.getNumero());
        }

        return documentoRepository.save(documento);
    }

    /**
     * Actualiza un documento existente
     */
    public Documento update(Long id, Documento documento) {
        Optional<Documento> documentoExistente = documentoRepository.findById(id);
        if (documentoExistente.isPresent()) {
            // Validar que el número de documento no exista si se está cambiando
            if (!documento.getNumero().equals(documentoExistente.get().getNumero())) {
                if (documentoRepository.existsByNumero(documento.getNumero())) {
                    throw new RuntimeException("Ya existe un documento con el número: " + documento.getNumero());
                }
            }

            documento.setId(id);
            return documentoRepository.save(documento);
        } else {
            throw new RuntimeException("Documento no encontrado con ID: " + id);
        }
    }

    /**
     * Elimina un documento
     */
    public void deleteById(Long id) {
        if (!documentoRepository.existsById(id)) {
            throw new RuntimeException("Documento no encontrado con ID: " + id);
        }
        documentoRepository.deleteById(id);
    }

    /**
     * Verifica si existe un documento con el número dado
     */
    public boolean existsByNumero(String numero) {
        return documentoRepository.existsByNumero(numero);
    }
} 