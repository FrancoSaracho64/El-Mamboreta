package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {
    
    List<MateriaPrima> findByActivoTrue();
    
    List<MateriaPrima> findByNombreContainingIgnoreCase(String nombre);
    
    List<MateriaPrima> findByPrecioBetween(Double precioMin, Double precioMax);
    
    List<MateriaPrima> findByStockLessThan(Integer stockMinimo);
    
    List<MateriaPrima> findByUnidadMedida(String unidadMedida);
    
    @Query("SELECT mp FROM MateriaPrima mp WHERE mp.stock = 0 AND mp.activo = true")
    List<MateriaPrima> findMateriasPrimasSinStock();
    
    @Query("SELECT mp FROM MateriaPrima mp WHERE mp.precio >= :precioMin AND mp.precio <= :precioMax AND mp.activo = true")
    List<MateriaPrima> findMateriasPrimasPorRangoPrecio(@Param("precioMin") Double precioMin, 
                                                       @Param("precioMax") Double precioMax);
}
