package com.mamboreta.backend.dto;

import java.util.List;

public class PedidoDTO {
    private Long clienteId;
    private String estado;
    private List<PedidoProductoDTO> productos;

    // Para enviar el cliente completo al front
    private ClienteDTO cliente;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<PedidoProductoDTO> getProductos() { return productos; }
    public void setProductos(List<PedidoProductoDTO> productos) { this.productos = productos; }

    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }
}
