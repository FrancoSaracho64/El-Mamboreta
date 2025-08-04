package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByActivoTrue();
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
    
    List<Producto> findByStockLessThan(Integer stockMinimo);
    
    @Query("SELECT p FROM Producto p WHERE p.stock = 0 AND p.activo = true")
    List<Producto> findProductosSinStock();
    
    @Query("SELECT p FROM Producto p WHERE p.precio >= :precioMin AND p.precio <= :precioMax AND p.activo = true")
    List<Producto> findProductosPorRangoPrecio(@Param("precioMin") Double precioMin, 
                                              @Param("precioMax") Double precioMax);
    
    @Query("SELECT p FROM Producto p JOIN p.materiasPrimas mp WHERE mp.id = :materiaPrimaId")
    List<Producto> findByMateriaPrimaId(@Param("materiaPrimaId") Long materiaPrimaId);
}
