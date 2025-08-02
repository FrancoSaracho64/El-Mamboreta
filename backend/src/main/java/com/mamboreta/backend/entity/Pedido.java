package com.mamboreta.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad JPA que representa un pedido.
 * Incluye una relación con Cliente y con la lista de productos del pedido.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación ManyToMany con la entidad Producto.
     * Un pedido puede tener muchos productos y un producto puede estar en muchos pedidos.
     * Se usa @JoinTable para crear una tabla intermedia que gestiona la relación.
     */
    @ManyToMany
    @JoinTable(
            name = "pedido_productos",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;

    /**
     * Relación ManyToOne con la entidad Cliente.
     * Muchos pedidos pueden pertenecer a un solo cliente.
     * Se usa @JoinColumn para especificar la clave foránea.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String estado;
    /**
     * La fecha y hora en que se solicitó el pedido.
     * @CreationTimestamp se encarga de asignar automáticamente la fecha al crear el registro.
     */
    @CreationTimestamp
    private LocalDateTime fechaSolicitado;
}
