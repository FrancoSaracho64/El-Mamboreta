package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    List<Cliente> findByActivoTrue();
    
    Optional<Cliente> findByEmail(String email);
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%")
    List<Cliente> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Cliente c JOIN c.documentos d WHERE d.numero = :numeroDocumento")
    Optional<Cliente> findByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);
    
    @Query("SELECT c FROM Cliente c JOIN c.documentos d WHERE d.tipo = :tipoDocumento AND d.numero = :numeroDocumento")
    Optional<Cliente> findByTipoAndNumeroDocumento(@Param("tipoDocumento") String tipoDocumento, 
                                                   @Param("numeroDocumento") String numeroDocumento);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(c) > 0 FROM Cliente c JOIN c.documentos d WHERE d.numero = :numeroDocumento")
    boolean existsByDocumentoNumero(@Param("numeroDocumento") String numeroDocumento);
}
