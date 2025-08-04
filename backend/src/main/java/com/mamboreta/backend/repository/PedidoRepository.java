package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteId(Long clienteId);
    
    List<Pedido> findByEstado(String estado);
    
    List<Pedido> findByFechaSolicitadoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    List<Pedido> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, @Param("estado") String estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaSolicitado >= :fechaInicio AND p.fechaSolicitado <= :fechaFin AND p.estado = :estado")
    List<Pedido> findByFechaSolicitadoBetweenAndEstado(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                      @Param("fechaFin") LocalDateTime fechaFin, 
                                                      @Param("estado") String estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<Pedido> findPedidosActivos();
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente.nombre LIKE %:nombreCliente% OR p.cliente.apellido LIKE %:nombreCliente%")
    List<Pedido> findByNombreClienteContaining(@Param("nombreCliente") String nombreCliente);
}
