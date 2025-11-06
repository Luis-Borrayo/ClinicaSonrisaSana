package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "pacientes")
public class Pacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false, length = 13)
    private String dpi; // Cambiado a String para mejor manejo

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

    // EDAD SE CALCULA AUTOMÁTICAMENTE - NO SE PERSISTE
    @Transient
    private Integer edad;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String contacto;

    // AGREGADO: Campo correo (si lo necesitas)
    @Email(message = "Debe ingresar un correo válido")
    @Size(max = 100)
    @Column(length = 100)
    private String correo;

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
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @NotNull
    @Column(name = "fecha_edicion", nullable = false)
    private LocalDate fechaEdicion;

    public Pacientes() {}

    public Integer getEdad() {
        if (fechaNacimiento != null) {
            return Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
        return null;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
        fechaEdicion = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaEdicion = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
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

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // NO hay setter para edad - se calcula automáticamente

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    @Override
    public String toString() {
        return "Pacientes{" +
                "id=" + id +
                ", dpi='" + dpi + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad=" + getEdad() +
                ", contacto='" + contacto + '\'' +
                '}';
    }
}