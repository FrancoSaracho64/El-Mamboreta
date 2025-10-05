package mamboreta.controller;

import com.mamboreta.backend.entity.Venta;
import com.mamboreta.backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    /**
     * Obtiene todas las ventas
     */
    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() {
        List<Venta> ventas = ventaService.findAll();
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene una venta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.findById(id);
        return venta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca ventas por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> getVentasByCliente(@PathVariable Long clienteId) {
        List<Venta> ventas = ventaService.findByClienteId(clienteId);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Busca ventas por rango de fechas
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<Venta>> getVentasByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<Venta> ventas = ventaService.findByFechaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Busca ventas por rango de fechas de entrega
     */
    @GetMapping("/fechas-entrega")
    public ResponseEntity<List<Venta>> getVentasByFechaEntregaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<Venta> ventas = ventaService.findByFechaEntregaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Busca ventas por rango de precio
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Venta>> getVentasByPrecioRange(
            @RequestParam Double precioMin, 
            @RequestParam Double precioMax) {
        List<Venta> ventas = ventaService.findByPrecioVentaBetween(precioMin, precioMax);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Busca ventas por nombre del cliente
     */
    @GetMapping("/cliente-nombre")
    public ResponseEntity<List<Venta>> getVentasByNombreCliente(@RequestParam String nombreCliente) {
        List<Venta> ventas = ventaService.findByNombreClienteContaining(nombreCliente);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Crea una nueva venta
     */
    @PostMapping
    public ResponseEntity<Venta> createVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.save(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza una venta existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Venta> updateVenta(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Venta ventaActualizada = ventaService.update(id, venta);
            return ResponseEntity.ok(ventaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina una venta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            ventaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Registra la fecha de entrega de una venta
     */
    @PutMapping("/{id}/entrega")
    public ResponseEntity<Venta> registrarEntrega(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEntrega) {
        try {
            Venta venta = ventaService.registrarEntrega(id, fechaEntrega);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calcula el total de ventas en un período
     */
    @GetMapping("/total")
    public ResponseEntity<Double> calcularTotalVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        Double total = ventaService.calcularTotalVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Cuenta el número de ventas en un período
     */
    @GetMapping("/contar")
    public ResponseEntity<Long> contarVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        Long count = ventaService.contarVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(count);
    }

    /**
     * Calcula el promedio de ventas en un período
     */
    @GetMapping("/promedio")
    public ResponseEntity<Double> calcularPromedioVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        Double promedio = ventaService.calcularPromedioVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(promedio);
    }

    /**
     * Obtiene estadísticas de ventas para un período
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<VentaService.VentaStats> obtenerEstadisticasVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        VentaService.VentaStats stats = ventaService.obtenerEstadisticasVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(stats);
    }
} 