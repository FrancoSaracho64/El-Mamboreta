package com.mamboreta.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "red_social")
public class RedSocial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la red social es obligatorio")
    @Column(nullable = false)
    private String red; // Facebook, Instagram, Twitter, etc.
    
    @NotBlank(message = "El usuario es obligatorio")
    @Column(nullable = false)
    private String usuario;
    
    @Column(length = 200)
    private String url;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
