package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @NotNull
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "duracion", nullable = false)
    private int duracion;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Odontologo.Especialidad especialidad;

    public Tratamiento() {}

    public Tratamiento(Odontologo odontologo, String descripcion, int duracion, BigDecimal costo, Odontologo.Especialidad especialidad) {
        this.odontologo = odontologo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.costo = costo;
        this.especialidad = especialidad;
    }

    public Long getId() {
        return id;}
    public void setId(Long id) {
        this.id = id;}

    public Odontologo getOdontologo() {
        return odontologo;}
    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;}

    public String getDescripcion() {
        return descripcion;}
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;}

    public int getDuracion() {
        return duracion;}
    public void setDuracion(int duracion) {
        this.duracion = duracion;}

    public BigDecimal getCosto() {
        return costo;}
    public void setCosto(BigDecimal costo) {
        this.costo = costo;}

    public Odontologo.Especialidad getEspecialidad() {
        return especialidad;}
    public void setEspecialidad(Odontologo.Especialidad especialidad) {
        this.especialidad = especialidad;}
}