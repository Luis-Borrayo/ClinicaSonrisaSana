package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "odontologos")
public class Odontologo {

    public enum Especialidad {
        ODONTOLOGO_GENERAL,
        ODONTOPEDIATRIA,
        ORTODONCIA,
        ENDODONCIA,
        PERIODONCIA,
        CIRUGIA_ORAL_Y_MAXILOFACIAL,
        PROSTODONCIA,
        IMPLANTOLOGIA_ORAL,
        ODONTOLOGIA_ESTETICA
    }

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidad especialidad;

    public Odontologo() {}

    public Odontologo(User usuario, String colegiado, String antiguedad, Especialidad especialidad) {
        this.usuario = usuario;
        this.colegiado = colegiado;
        this.antiguedad = antiguedad;
        this.especialidad = especialidad;
    }

    // ✅ MÉTODOS HELPER PARA MOSTRAR EN LA INTERFAZ
    public String getNombreCompleto() {
        if (usuario != null) {
            return usuario.getNombres() + " " + usuario.getApellidos() + " - " + especialidad;
        }
        return "Odontólogo " + colegiado + " - " + especialidad;
    }

    // ✅ ESTE MÉTODO ES NECESARIO PARA LA TABLA
    public String getNombre() {
        if (usuario != null) {
            return usuario.getNombres() + " " + usuario.getApellidos();
        }
        return "Odontólogo " + colegiado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }
    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getColegiado() {
        return colegiado;
    }
    public void setColegiado(String colegiado) {
        this.colegiado = colegiado;
    }

    public String getAntiguedad() {
        return antiguedad;
    }
    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
    }

    public Boolean getActivo() {
        // Asumimos que si el usuario está activo, el odontólogo también lo está
        return usuario != null && usuario.isActive();
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return getNombre() + " - " + especialidad;
    }
}