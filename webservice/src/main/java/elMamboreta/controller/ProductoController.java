package elMamboreta.controller;

import com.mamboreta.backend.entity.Producto;
import com.mamboreta.backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Obtiene todos los productos activos
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoService.findAllActivos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene todos los productos (incluyendo inactivos)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Producto>> getAllProductosIncludingInactive() {
        List<Producto> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findById(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca productos por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<Producto>> searchProductos(@RequestParam String nombre) {
        List<Producto> productos = productoService.findByNombreContaining(nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos por rango de precio
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> getProductosByPrecioRange(
            @RequestParam Double precioMin, 
            @RequestParam Double precioMax) {
        List<Producto> productos = productoService.findByPrecioBetween(precioMin, precioMax);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos con stock bajo
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> getProductosConStockBajo(@RequestParam Integer stockMinimo) {
        List<Producto> productos = productoService.findByStockLessThan(stockMinimo);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos sin stock
     */
    @GetMapping("/sin-stock")
    public ResponseEntity<List<Producto>> getProductosSinStock() {
        List<Producto> productos = productoService.findProductosSinStock();
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos por materia prima
     */
    @GetMapping("/materia-prima/{materiaPrimaId}")
    public ResponseEntity<List<Producto>> getProductosByMateriaPrima(@PathVariable Long materiaPrimaId) {
        List<Producto> productos = productoService.findByMateriaPrimaId(materiaPrimaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * Crea un nuevo producto
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.update(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva un producto (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza el stock de un producto
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Producto> updateStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        try {
            Producto producto = productoService.updateStock(id, nuevoStock);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Incrementa el stock de un producto
     */
    @PutMapping("/{id}/incrementar-stock")
    public ResponseEntity<Producto> incrementarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            Producto producto = productoService.incrementarStock(id, cantidad);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Decrementa el stock de un producto
     */
    @PutMapping("/{id}/decrementar-stock")
    public ResponseEntity<Producto> decrementarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            Producto producto = productoService.decrementarStock(id, cantidad);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 