package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Long> {
    
    List<Telefono> findByActivoTrue();
    
    List<Telefono> findByNumeroContaining(String numero);
    
    List<Telefono> findByTipo(String tipo);
}
