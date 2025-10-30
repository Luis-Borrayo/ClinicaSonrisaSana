package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    public Administrador() {}

    public Administrador(User usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public User getUsuario() {
        return usuario; }
    public void setUsuario(User usuario) {
        this.usuario = usuario; }
}