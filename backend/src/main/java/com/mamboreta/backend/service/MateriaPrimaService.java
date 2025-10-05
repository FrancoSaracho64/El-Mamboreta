package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.MateriaPrima;
import com.mamboreta.backend.repository.MateriaPrimaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MateriaPrimaService {

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    /**
     * Obtiene todas las materias primas activas
     */
    public List<MateriaPrima> findAllActivos() {
        return materiaPrimaRepository.findByActivoTrue();
    }

    /**
     * Obtiene todas las materias primas
     */
    public List<MateriaPrima> findAll() {
        return materiaPrimaRepository.findAll();
    }

    /**
     * Busca una materia prima por ID
     */
    public Optional<MateriaPrima> findById(Long id) {
        return materiaPrimaRepository.findById(id);
    }

    /**
     * Busca materias primas por nombre
     */
    public List<MateriaPrima> findByNombreContaining(String nombre) {
        return materiaPrimaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca materias primas con stock bajo
     */
    public List<MateriaPrima> findByStockLessThan(Integer stockMinimo) {
        return materiaPrimaRepository.findByStockLessThan(stockMinimo);
    }

    /**
     * Busca materias primas sin stock
     */
    public List<MateriaPrima> findMateriasPrimasSinStock() {
        return materiaPrimaRepository.findMateriasPrimasSinStock();
    }

    /**
     * Busca materias primas por unidad de medida
     */
    public List<MateriaPrima> findByUnidadMedida(String unidadMedida) {
        return materiaPrimaRepository.findByUnidadMedida(unidadMedida);
    }

    /**
     * Guarda una nueva materia prima
     */
    public MateriaPrima save(MateriaPrima materiaPrima) {
        if (materiaPrima.getStock() < 0) {
            throw new RuntimeException("El stock de la materia prima no puede ser negativo");
        }

        return materiaPrimaRepository.save(materiaPrima);
    }

    /**
     * Actualiza una materia prima existente
     */
    public MateriaPrima update(Long id, MateriaPrima materiaPrima) {
        Optional<MateriaPrima> materiaPrimaExistente = materiaPrimaRepository.findById(id);
        if (materiaPrimaExistente.isPresent()) {
            if (materiaPrima.getStock() < 0) {
                throw new RuntimeException("El stock de la materia prima no puede ser negativo");
            }

            materiaPrima.setId(id);
            return materiaPrimaRepository.save(materiaPrima);
        } else {
            throw new RuntimeException("Materia prima no encontrada con ID: " + id);
        }
    }

    /**
     * Desactiva una materia prima (borrado lógico)
     */
    public void deleteById(Long id) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(id);
        if (materiaPrima.isPresent()) {
            MateriaPrima materiaPrimaActual = materiaPrima.get();
            materiaPrimaActual.setActivo(false);
            materiaPrimaRepository.save(materiaPrimaActual);
        } else {
            throw new RuntimeException("Materia prima no encontrada con ID: " + id);
        }
    }

    /**
     * Actualiza el stock de una materia prima
     */
    public MateriaPrima updateStock(Long id, Integer nuevoStock) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(id);
        if (materiaPrima.isPresent()) {
            MateriaPrima materiaPrimaActual = materiaPrima.get();
            if (nuevoStock < 0) {
                throw new RuntimeException("El stock no puede ser negativo");
            }
            materiaPrimaActual.setStock(nuevoStock);
            return materiaPrimaRepository.save(materiaPrimaActual);
        } else {
            throw new RuntimeException("Materia prima no encontrada con ID: " + id);
        }
    }

    /**
     * Incrementa el stock de una materia prima
     */
    public MateriaPrima incrementarStock(Long id, Integer cantidad) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(id);
        if (materiaPrima.isPresent()) {
            MateriaPrima materiaPrimaActual = materiaPrima.get();
            if (cantidad < 0) {
                throw new RuntimeException("La cantidad a incrementar no puede ser negativa");
            }
            materiaPrimaActual.setStock(materiaPrimaActual.getStock() + cantidad);
            return materiaPrimaRepository.save(materiaPrimaActual);
        } else {
            throw new RuntimeException("Materia prima no encontrada con ID: " + id);
        }
    }

    /**
     * Decrementa el stock de una materia prima
     */
    public MateriaPrima decrementarStock(Long id, Integer cantidad) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(id);
        if (materiaPrima.isPresent()) {
            MateriaPrima materiaPrimaActual = materiaPrima.get();
            if (cantidad < 0) {
                throw new RuntimeException("La cantidad a decrementar no puede ser negativa");
            }
            if (materiaPrimaActual.getStock() < cantidad) {
                throw new RuntimeException("No hay suficiente stock disponible");
            }
            materiaPrimaActual.setStock(materiaPrimaActual.getStock() - cantidad);
            return materiaPrimaRepository.save(materiaPrimaActual);
        } else {
            throw new RuntimeException("Materia prima no encontrada con ID: " + id);
        }
    }
} 