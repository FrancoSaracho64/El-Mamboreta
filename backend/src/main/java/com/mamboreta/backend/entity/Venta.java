package com.mamboreta.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una venta.
 * Una venta es el resultado de un pedido que ha sido entregado.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación OneToOne con la entidad Pedido.
     * Una venta corresponde a un único pedido.
     * La anotación @JoinColumn se usa para la clave foránea en la tabla 'venta'.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    private Double precioVenta;

    /**
     * La fecha de entrega del producto.
     * La columna puede ser nula ya que la fecha de entrega podría no estar
     * disponible al momento de crear el registro de venta.
     */
    @Column(nullable = true)
    private LocalDateTime fechaEntrega;

    /**
     * Campo para observaciones sobre la venta.
     * La columna es opcional.
     */
    @Column(nullable = true)
    private String observaciones;

    /**
     * La fecha de creación del registro de venta.
     * @CreationTimestamp asigna automáticamente la fecha y hora.
     */
    @CreationTimestamp
    private LocalDateTime fecha;
}
