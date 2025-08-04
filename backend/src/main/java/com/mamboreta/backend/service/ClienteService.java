package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Cliente;
import com.mamboreta.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes activos
     */
    public List<Cliente> findAllActivos() {
        return clienteRepository.findByActivoTrue();
    }

    /**
     * Obtiene todos los clientes
     */
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por ID
     */
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Busca un cliente por email
     */
    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Busca clientes por nombre o apellido
     */
    public List<Cliente> findByNombreContaining(String nombre) {
        return clienteRepository.findByNombreContaining(nombre);
    }

    /**
     * Busca un cliente por número de documento
     */
    public Optional<Cliente> findByNumeroDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumento(numeroDocumento);
    }

    /**
     * Guarda un nuevo cliente
     */
    public Cliente save(Cliente cliente) {
        // Validar que el email no exista si se proporciona
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getEmail());
            }
        }

        // Validar que los documentos no existan si se proporcionan
        if (cliente.getDocumentos() != null) {
            for (var documento : cliente.getDocumentos()) {
                if (documento.getNumero() != null && clienteRepository.existsByDocumentoNumero(documento.getNumero())) {
                    throw new RuntimeException("Ya existe un cliente con el documento: " + documento.getNumero());
                }
            }
        }

        // Configurar valores por defecto
        cliente.setActivo(true);
        cliente.setFechaRegistro(java.time.LocalDateTime.now());

        // Configurar relaciones bidireccionales
        if (cliente.getDocumentos() != null) {
            for (var documento : cliente.getDocumentos()) {
                documento.setCliente(cliente);
            }
        }

        if (cliente.getTelefonos() != null) {
            for (var telefono : cliente.getTelefonos()) {
                telefono.setCliente(cliente);
            }
        }

        if (cliente.getRedesSociales() != null) {
            for (var redSocial : cliente.getRedesSociales()) {
                redSocial.setCliente(cliente);
            }
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Actualiza un cliente existente
     */
    public Cliente update(Long id, Cliente cliente) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);
        if (clienteExistente.isPresent()) {
            Cliente clienteActual = clienteExistente.get();
            
            // Validar email único si se está cambiando
            if (cliente.getEmail() != null && !cliente.getEmail().equals(clienteActual.getEmail())) {
                if (clienteRepository.existsByEmail(cliente.getEmail())) {
                    throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getEmail());
                }
            }

            // Validar documentos únicos si se están cambiando
            if (cliente.getDocumentos() != null) {
                for (var documento : cliente.getDocumentos()) {
                    if (documento.getNumero() != null) {
                        // Verificar si el documento ya existe en otro cliente
                        Optional<Cliente> clienteConDocumento = clienteRepository.findByNumeroDocumento(documento.getNumero());
                        if (clienteConDocumento.isPresent() && !clienteConDocumento.get().getId().equals(id)) {
                            throw new RuntimeException("Ya existe un cliente con el documento: " + documento.getNumero());
                        }
                    }
                }
            }

            cliente.setId(id);
            
            // Configurar relaciones bidireccionales
            if (cliente.getDocumentos() != null) {
                for (var documento : cliente.getDocumentos()) {
                    documento.setCliente(cliente);
                }
            }

            if (cliente.getTelefonos() != null) {
                for (var telefono : cliente.getTelefonos()) {
                    telefono.setCliente(cliente);
                }
            }

            if (cliente.getRedesSociales() != null) {
                for (var redSocial : cliente.getRedesSociales()) {
                    redSocial.setCliente(cliente);
                }
            }

            return clienteRepository.save(cliente);
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }

    /**
     * Desactiva un cliente (borrado lógico)
     */
    public void deleteById(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            Cliente clienteActual = cliente.get();
            clienteActual.setActivo(false);
            clienteRepository.save(clienteActual);
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }

    /**
     * Verifica si existe un cliente con el email dado
     */
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    /**
     * Verifica si existe un cliente con el número de documento dado
     */
    public boolean existsByDocumentoNumero(String numeroDocumento) {
        return clienteRepository.existsByDocumentoNumero(numeroDocumento);
    }
} 