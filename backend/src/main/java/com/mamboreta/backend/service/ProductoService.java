package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Producto;
import com.mamboreta.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos activos
     */
    public List<Producto> findAllActivos() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Obtiene todos los productos
     */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por ID
     */
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Busca productos por nombre
     */
    public List<Producto> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca productos por rango de precio
     */
    public List<Producto> findByPrecioBetween(Double precioMin, Double precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax);
    }


    /**
     * Busca productos con stock bajo
     */
    public List<Producto> findByStockLessThan(Integer stockMinimo) {
        return productoRepository.findByStockLessThan(stockMinimo);
    }

    /**
     * Decrementa el stock de un producto
     */
    public void decrementarStock(Long productoId, int cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            int nuevoStock = producto.getStock() - cantidad;
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
        }
    }

    /**
     * Busca productos sin stock
     */
    public List<Producto> findProductosSinStock() {
        return productoRepository.findProductosSinStock();
    }

    /**
     * Busca productos por materia prima
     */
    public List<Producto> findByMateriaPrimaId(Long materiaPrimaId) {
        return productoRepository.findByMateriaPrimaId(materiaPrimaId);
    }

    /**
     * Guarda un nuevo producto
     */
    public Producto save(Producto producto) {
        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a 0");
        }

        if (producto.getStock() < 0) {
            throw new RuntimeException("El stock del producto no puede ser negativo");
        }

        return productoRepository.save(producto);
    }

    /**
     * Actualiza un producto existente
     */
    public Producto update(Long id, Producto producto) {
        Optional<Producto> productoExistente = productoRepository.findById(id);
        if (productoExistente.isPresent()) {
            if (producto.getPrecio() <= 0) {
                throw new RuntimeException("El precio del producto debe ser mayor a 0");
            }

            if (producto.getStock() < 0) {
                throw new RuntimeException("El stock del producto no puede ser negativo");
            }

            producto.setId(id);
            return productoRepository.save(producto);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    /**
     * Desactiva un producto (borrado lógico)
     */
    public void deleteById(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto productoActual = producto.get();
            productoActual.setActivo(false);
            productoRepository.save(productoActual);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    /**
     * Actualiza el stock de un producto
     */
    public Producto updateStock(Long id, Integer nuevoStock) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto productoActual = producto.get();
            if (nuevoStock < 0) {
                throw new RuntimeException("El stock no puede ser negativo");
            }
            productoActual.setStock(nuevoStock);
            return productoRepository.save(productoActual);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    /**
     * Incrementa el stock de un producto
     */
    public Producto incrementarStock(Long id, Integer cantidad) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto productoActual = producto.get();
            if (cantidad < 0) {
                throw new RuntimeException("La cantidad a incrementar no puede ser negativa");
            }
            productoActual.setStock(productoActual.getStock() + cantidad);
            return productoRepository.save(productoActual);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    /**
     * Decrementa el stock de un producto
     */
    public Producto decrementarStock(Long id, Integer cantidad) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto productoActual = producto.get();
            if (cantidad < 0) {
                throw new RuntimeException("La cantidad a decrementar no puede ser negativa");
            }
            if (productoActual.getStock() < cantidad) {
                throw new RuntimeException("No hay suficiente stock disponible");
            }
            productoActual.setStock(productoActual.getStock() - cantidad);
            return productoRepository.save(productoActual);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    public List<Producto> findAllById(List<Long> productoIds) {
        return productoRepository.findAllById(productoIds);
    }
}