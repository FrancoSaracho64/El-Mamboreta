package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByPedidoClienteId(Long clienteId);
    
    List<Venta> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Venta> findByFechaEntregaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v WHERE v.precioVenta >= :precioMin AND v.precioVenta <= :precioMax")
    List<Venta> findByPrecioVentaBetween(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax);
    
    @Query("SELECT v FROM Venta v WHERE v.pedido.cliente.nombre LIKE %:nombreCliente% OR v.pedido.cliente.apellido LIKE %:nombreCliente%")
    List<Venta> findByNombreClienteContaining(@Param("nombreCliente") String nombreCliente);
    
    @Query("SELECT SUM(v.precioVenta) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    Double sumPrecioVentaByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countVentasByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                  @Param("fechaFin") LocalDateTime fechaFin);
}
