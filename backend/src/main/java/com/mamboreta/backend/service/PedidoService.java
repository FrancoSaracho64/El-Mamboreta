package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Pedido;
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
        // Validar que el pedido tenga productos
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un producto");
        }

        // Validar que el cliente exista
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new RuntimeException("El pedido debe tener un cliente válido");
        }

        // Validar stock de productos
        for (Producto producto : pedido.getProductos()) {
            Optional<Producto> productoExistente = productoService.findById(producto.getId());
            if (productoExistente.isPresent()) {
                Producto prod = productoExistente.get();
                if (prod.getStock() <= 0) {
                    throw new RuntimeException("El producto " + prod.getNombre() + " no tiene stock disponible");
                }
            } else {
                throw new RuntimeException("El producto con ID " + producto.getId() + " no existe");
            }
        }

        // Establecer estado inicial si no se proporciona
        if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado("PENDIENTE");
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * Actualiza un pedido existente
     */
    public Pedido update(Long id, Pedido pedido) {
        Optional<Pedido> pedidoExistente = pedidoRepository.findById(id);
        if (pedidoExistente.isPresent()) {
            // Validar que el pedido tenga productos
            if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
                throw new RuntimeException("El pedido debe tener al menos un producto");
            }

            // Validar que el cliente exista
            if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
                throw new RuntimeException("El pedido debe tener un cliente válido");
            }

            pedido.setId(id);
            return pedidoRepository.save(pedido);
        } else {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
    }

    /**
     * Actualiza el estado de un pedido
     */
    public Pedido updateEstado(Long id, String nuevoEstado) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido pedidoActual = pedido.get();
            
            // Validar transiciones de estado válidas
            String estadoActual = pedidoActual.getEstado();
            if (!isTransicionEstadoValida(estadoActual, nuevoEstado)) {
                throw new RuntimeException("Transición de estado no válida: " + estadoActual + " -> " + nuevoEstado);
            }

            // Si el estado cambia a COMPLETADO, actualizar stock de productos
            if ("COMPLETADO".equals(nuevoEstado) && !"COMPLETADO".equals(estadoActual)) {
                for (Producto producto : pedidoActual.getProductos()) {
                    productoService.decrementarStock(producto.getId(), 1);
                }
            }

            pedidoActual.setEstado(nuevoEstado);
            return pedidoRepository.save(pedidoActual);
        } else {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
    }

    /**
     * Elimina un pedido
     */
    public void deleteById(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            // Solo permitir eliminar pedidos en estado PENDIENTE
            if (!"PENDIENTE".equals(pedido.get().getEstado())) {
                throw new RuntimeException("Solo se pueden eliminar pedidos en estado PENDIENTE");
            }
            pedidoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
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
            case "COMPLETADO":
            case "CANCELADO":
                return false; // Estados finales, no se pueden cambiar
            default:
                return false;
        }
    }

    /**
     * Calcula el total del pedido
     */
    public Double calcularTotalPedido(Long pedidoId) {
        Optional<Pedido> pedido = pedidoRepository.findById(pedidoId);
        if (pedido.isPresent()) {
            return pedido.get().getProductos().stream()
                    .mapToDouble(Producto::getPrecio)
                    .sum();
        }
        return 0.0;
    }
} 