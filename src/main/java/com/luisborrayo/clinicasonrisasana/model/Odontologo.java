package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "odontologo")
public class Odontologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @NotBlank
    @Pattern(regexp = "C-\\d{4}", message = "Formato correcto: C-1234")
    @Column(unique = true, nullable = false)
    private String colegiado;

    @NotBlank
    @Column(nullable = false)
    private String antiguedad;

    @NotBlank
    @Column(nullable = false)
    private String especialidad;

    public Odontologo() {}

    public Odontologo(User usuario, String colegiado, String antiguedad, String especialidad) {
        this.usuario = usuario;
        this.colegiado = colegiado;
        this.antiguedad = antiguedad;
        this.especialidad = especialidad;
    }

    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public User getUsuario() {
        return usuario; }
    public void setUsuario(User usuario) {
        this.usuario = usuario; }

    public String getColegiado() {
        return colegiado; }
    public void setColegiado(String colegiado) {
        this.colegiado = colegiado; }

    public String getAntiguedad() {
        return antiguedad; }
    public void setAntiguedad(String antiguedad) { this.antiguedad = antiguedad; }

    public String getEspecialidad() {
        return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}