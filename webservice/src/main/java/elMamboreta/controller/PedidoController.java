package elMamboreta.controller;

import com.mamboreta.backend.entity.Pedido;
import com.mamboreta.backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Obtiene todos los pedidos
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtiene un pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.findById(id);
        return pedido.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca pedidos por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> getPedidosByCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca pedidos por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> getPedidosByEstado(@PathVariable String estado) {
        List<Pedido> pedidos = pedidoService.findByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca pedidos por rango de fechas
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<Pedido>> getPedidosByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<Pedido> pedidos = pedidoService.findByFechaSolicitadoBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca pedidos activos (PENDIENTE, EN_PROCESO)
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Pedido>> getPedidosActivos() {
        List<Pedido> pedidos = pedidoService.findPedidosActivos();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca pedidos por nombre del cliente
     */
    @GetMapping("/cliente-nombre")
    public ResponseEntity<List<Pedido>> getPedidosByNombreCliente(@RequestParam String nombreCliente) {
        List<Pedido> pedidos = pedidoService.findByNombreClienteContaining(nombreCliente);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Crea un nuevo pedido
     */
    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.save(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un pedido existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido pedidoActualizado = pedidoService.update(id, pedido);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza el estado de un pedido
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> updateEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        try {
            Pedido pedido = pedidoService.updateEstado(id, nuevoEstado);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        try {
            pedidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calcula el total del pedido
     */
    @GetMapping("/{id}/total")
    public ResponseEntity<Double> calcularTotalPedido(@PathVariable Long id) {
        Double total = pedidoService.calcularTotalPedido(id);
        return ResponseEntity.ok(total);
    }
} 