package com.mamboreta.backend.service;

import com.mamboreta.backend.entity.Pedido;
import com.mamboreta.backend.entity.Venta;
import com.mamboreta.backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Obtiene todas las ventas
     */
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    /**
     * Busca una venta por ID
     */
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    /**
     * Busca ventas por cliente
     */
    public List<Venta> findByClienteId(Long clienteId) {
        return ventaRepository.findByPedidoClienteId(clienteId);
    }

    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    /**
     * Busca ventas por rango de fechas de entrega
     */
    public List<Venta> findByFechaEntregaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaEntregaBetween(fechaInicio, fechaFin);
    }

    /**
     * Busca ventas por rango de precio
     */
    public List<Venta> findByPrecioVentaBetween(Double precioMin, Double precioMax) {
        return ventaRepository.findByPrecioVentaBetween(precioMin, precioMax);
    }

    /**
     * Busca ventas por nombre del cliente
     */
    public List<Venta> findByNombreClienteContaining(String nombreCliente) {
        return ventaRepository.findByNombreClienteContaining(nombreCliente);
    }

    /**
     * Guarda una nueva venta
     */
    public Venta save(Venta venta) {
        // Validar que la venta tenga un pedido
        if (venta.getPedido() == null || venta.getPedido().getId() == null) {
            throw new RuntimeException("La venta debe tener un pedido válido");
        }

        // Validar que el pedido exista y esté completado
        Optional<Pedido> pedido = pedidoService.findById(venta.getPedido().getId());
        if (pedido.isEmpty()) {
            throw new RuntimeException("El pedido no existe");
        }

        if (!"COMPLETADO".equals(pedido.get().getEstado())) {
            throw new RuntimeException("Solo se pueden crear ventas de pedidos completados");
        }

        // Validar que el precio de venta sea válido
        if (venta.getPrecioVenta() == null || venta.getPrecioVenta() <= 0) {
            throw new RuntimeException("El precio de venta debe ser mayor a 0");
        }

        // Verificar que no exista ya una venta para este pedido
        if (ventaRepository.findByPedidoClienteId(pedido.get().getCliente().getId()).stream()
                .anyMatch(v -> v.getPedido().getId().equals(venta.getPedido().getId()))) {
            throw new RuntimeException("Ya existe una venta para este pedido");
        }

        return ventaRepository.save(venta);
    }

    /**
     * Actualiza una venta existente
     */
    public Venta update(Long id, Venta venta) {
        Optional<Venta> ventaExistente = ventaRepository.findById(id);
        if (ventaExistente.isPresent()) {
            // Validar que el precio de venta sea válido
            if (venta.getPrecioVenta() == null || venta.getPrecioVenta() <= 0) {
                throw new RuntimeException("El precio de venta debe ser mayor a 0");
            }

            venta.setId(id);
            return ventaRepository.save(venta);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    /**
     * Elimina una venta
     */
    public void deleteById(Long id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        if (venta.isPresent()) {
            ventaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    /**
     * Registra la fecha de entrega de una venta
     */
    public Venta registrarEntrega(Long id, LocalDateTime fechaEntrega) {
        Optional<Venta> venta = ventaRepository.findById(id);
        if (venta.isPresent()) {
            Venta ventaActual = venta.get();
            ventaActual.setFechaEntrega(fechaEntrega);
            return ventaRepository.save(ventaActual);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    /**
     * Calcula el total de ventas en un período
     */
    public Double calcularTotalVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = ventaRepository.sumPrecioVentaByFechaBetween(fechaInicio, fechaFin);
        return total != null ? total : 0.0;
    }

    /**
     * Cuenta el número de ventas en un período
     */
    public Long contarVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Long count = ventaRepository.countVentasByFechaBetween(fechaInicio, fechaFin);
        return count != null ? count : 0L;
    }

    /**
     * Calcula el promedio de ventas en un período
     */
    public Double calcularPromedioVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = calcularTotalVentas(fechaInicio, fechaFin);
        Long count = contarVentas(fechaInicio, fechaFin);
        
        if (count > 0) {
            return total / count;
        }
        return 0.0;
    }

    /**
     * Obtiene estadísticas de ventas para un período
     */
    public VentaStats obtenerEstadisticasVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = calcularTotalVentas(fechaInicio, fechaFin);
        Long count = contarVentas(fechaInicio, fechaFin);
        Double promedio = calcularPromedioVentas(fechaInicio, fechaFin);

        return new VentaStats(total, count, promedio);
    }

    /**
     * Clase interna para estadísticas de ventas
     */
    public static class VentaStats {
        private final Double totalVentas;
        private final Long cantidadVentas;
        private final Double promedioVentas;

        public VentaStats(Double totalVentas, Long cantidadVentas, Double promedioVentas) {
            this.totalVentas = totalVentas;
            this.cantidadVentas = cantidadVentas;
            this.promedioVentas = promedioVentas;
        }

        public Double getTotalVentas() { return totalVentas; }
        public Long getCantidadVentas() { return cantidadVentas; }
        public Double getPromedioVentas() { return promedioVentas; }
    }
} 