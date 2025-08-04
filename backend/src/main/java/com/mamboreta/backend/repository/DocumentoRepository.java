package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    Optional<Documento> findByNumero(String numero);
    
    List<Documento> findByTipo(String tipo);
    
    boolean existsByNumero(String numero);
}
