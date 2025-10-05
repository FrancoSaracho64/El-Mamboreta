package com.mamboreta.backend.dto;

public class PedidoProductoDTO {
    private Long productoId;
    private Integer cantidad;

    public PedidoProductoDTO() {}

    public PedidoProductoDTO(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}