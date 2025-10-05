package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Pedido;
import com.mamboreta.backend.entity.PedidoProducto;
import com.mamboreta.backend.entity.Producto;
import com.mamboreta.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoService productoService;

    /**
     * Obtiene todos los pedidos
     */
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    /**
     * Busca un pedido por ID
     */
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Busca pedidos por cliente
     */
    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Busca pedidos por estado
     */
    public List<Pedido> findByEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    /**
     * Busca pedidos por rango de fechas
     */
    public List<Pedido> findByFechaSolicitadoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaSolicitadoBetween(fechaInicio, fechaFin);
    }

    /**
     * Busca pedidos activos (PENDIENTE, EN_PROCESO)
     */
    public List<Pedido> findPedidosActivos() {
        return pedidoRepository.findPedidosActivos();
    }

    /**
     * Busca pedidos por nombre del cliente
     */
    public List<Pedido> findByNombreClienteContaining(String nombreCliente) {
        return pedidoRepository.findByNombreClienteContaining(nombreCliente);
    }

    /**
     * Guarda un nuevo pedido
     */
    public Pedido save(Pedido pedido) {
        // Validar cliente
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new RuntimeException("El pedido debe tener un cliente válido");
        }

        // Validar productos y stock
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un producto");
        }

        for (PedidoProducto pp : pedido.getProductos()) {
            if (pp.getProducto() == null || pp.getProducto().getId() == null) {
                throw new RuntimeException("PedidoProducto debe tener un producto válido");
            }

            Producto productoExistente = productoService.findById(pp.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + pp.getProducto().getId()));

            if (productoExistente.getStock() < pp.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + productoExistente.getNombre());
            }

            // Vincular correctamente el PedidoProducto con el Pedido
            pp.setPedido(pedido);
            pp.setProducto(productoExistente);
        }

        // Estado inicial
        if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado("PENDIENTE");
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * Actualiza un pedido existente
     */
    public Pedido update(Long id, Pedido pedido) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        // Validar cliente
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new RuntimeException("El pedido debe tener un cliente válido");
        }

        // Validar productos
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un producto");
        }

        for (PedidoProducto pp : pedido.getProductos()) {
            if (pp.getProducto() == null || pp.getProducto().getId() == null) {
                throw new RuntimeException("PedidoProducto debe tener un producto válido");
            }
            Producto productoExistente = productoService.findById(pp.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + pp.getProducto().getId()));
            pp.setPedido(pedido);
            pp.setProducto(productoExistente);
        }

        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    /**
     * Actualiza el estado de un pedido
     */
    public Pedido updateEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        String estadoActual = pedido.getEstado();
        if (!isTransicionEstadoValida(estadoActual, nuevoEstado)) {
            throw new RuntimeException("Transición de estado no válida: " + estadoActual + " -> " + nuevoEstado);
        }

        // Si cambia a COMPLETADO, decrementar stock según cantidad
        if ("COMPLETADO".equals(nuevoEstado) && !"COMPLETADO".equals(estadoActual)) {
            for (PedidoProducto pp : pedido.getProductos()) {
                productoService.decrementarStock(pp.getProducto().getId(), pp.getCantidad());
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    /**
     * Elimina un pedido
     */
    public void deleteById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden eliminar pedidos en estado PENDIENTE");
        }

        pedidoRepository.deleteById(id);
    }

    /**
     * Calcula el total del pedido
     */
    public Double calcularTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        return pedido.getProductos().stream()
                .mapToDouble(pp -> pp.getProducto().getPrecio() * pp.getCantidad())
                .sum();
    }

    /**
     * Valida si la transición de estado es válida
     */
    private boolean isTransicionEstadoValida(String estadoActual, String nuevoEstado) {
        switch (estadoActual) {
            case "PENDIENTE":
                return "EN_PROCESO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado);
            case "EN_PROCESO":
                return "COMPLETADO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado);
            default:
                return false;
        }
    }
}
