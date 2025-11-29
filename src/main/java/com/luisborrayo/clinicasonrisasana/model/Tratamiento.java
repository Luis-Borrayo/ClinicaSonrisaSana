package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ CAMPO NOMBRE AGREGADO
    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Column(name = "duracion", nullable = false)
    private Integer duracion; // Duración en minutos

    @NotNull(message = "El costo es obligatorio")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @NotNull(message = "La especialidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Odontologo.Especialidad especialidad;

    // Constructores
    public Tratamiento() {}

    public Tratamiento(String nombre, Odontologo odontologo, String descripcion,
                       Integer duracion, BigDecimal costo, Odontologo.Especialidad especialidad) {
        this.nombre = nombre;
        this.odontologo = odontologo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.costo = costo;
        this.especialidad = especialidad;
    }

    // ✅ MÉTODOS AUXILIARES PARA LA VISTA
    public String getDuracionFormateada() {
        if (duracion == null) return "N/A";

        if (duracion < 60) {
            return duracion + " min";
        } else {
            int horas = duracion / 60;
            int minutos = duracion % 60;
            if (minutos == 0) {
                return horas + (horas == 1 ? " hora" : " horas");
            }
            return horas + "h " + minutos + "m";
        }
    }

    public String getCostoFormateado() {
        if (costo == null) return "Q 0.00";

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(costo).replace("GTQ", "Q");
    }

    public String getNombreOdontologo() {
        if (odontologo != null && odontologo.getUsuario() != null) {
            return odontologo.getUsuario().getNombres() + " " +
                    odontologo.getUsuario().getApellidos();
        }
        return "No asignado";
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Odontologo.Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Odontologo.Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "Tratamiento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", duracion=" + duracion + " min" +
                ", costo=" + costo +
                ", especialidad=" + especialidad +
                '}';
    }
}