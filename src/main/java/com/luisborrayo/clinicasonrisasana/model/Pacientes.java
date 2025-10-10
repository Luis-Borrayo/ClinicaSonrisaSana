package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Pacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false, length = 13)
    private Long dpi;

    @NotNull
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotNull
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String apellido;

    @NotNull
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotNull
    @Column(nullable = false)
    private Long edad;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String contacto;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String direccion;

    @Size(max = 255)
    @Column(length = 255)
    private String alergias;

    @Size(max = 255)
    @Column(length = 255)
    private String condiciones;

    @Size(max = 255)
    @Column(length = 255)
    private String observaciones;

    @ManyToOne(optional = false)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "seguro", nullable = false)
    private Facturas.Seguro seguro;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @NotNull
    @Column(name = "fecha_edicion", nullable = false)
    private LocalDate fechaEdicion;

    public Pacientes() {}

    public Pacientes(Long id, Long dpi, String nombre, String apellido, LocalDate fechaNacimiento,
                     Long edad, String contacto, String direccion, String alergias, String condiciones,
                     String observaciones, Odontologo odontologo, Facturas.Seguro seguro,
                     LocalDate fechaCreacion, LocalDate fechaEdicion) {
        this.id = id;
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
        this.contacto = contacto;
        this.direccion = direccion;
        this.alergias = alergias;
        this.condiciones = condiciones;
        this.observaciones = observaciones;
        this.odontologo = odontologo;
        this.seguro = seguro;
        this.fechaCreacion = fechaCreacion;
        this.fechaEdicion = fechaEdicion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDpi() {
        return dpi;
    }

    public void setDpi(Long dpi) {
        this.dpi = dpi;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getEdad() {
        return edad;
    }

    public void setEdad(Long edad) {
        this.edad = edad;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }

    public Facturas.Seguro getSeguro() {
        return seguro;
    }

    public void setSeguro(Facturas.Seguro seguro) {
        this.seguro = seguro;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDate fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }
}
