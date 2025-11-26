package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

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

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(name = "numero_telefono", nullable = false, length = 20)
    private String numeroTelefono;

    @Email
    @NotBlank
    @Column(nullable = false, length = 100)
    private String correo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidad especialidad;

    @NotBlank
    @Column(name = "numero_licencia", nullable = false, length = 50)
    private String numeroLicencia;

    @NotBlank
    @Column(name = "horario_trabajo", nullable = false, length = 100)
    private String horarioTrabajo;

    @NotNull
    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    @NotNull
    @Column(nullable = false)
    private Boolean activo;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String antiguedad;

    @NotBlank
    @Pattern(regexp = "C-\\d{4}", message = "Formato correcto: C-1234")
    @Column(unique = true, nullable = false, length = 20)
    private String colegiado;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    // Constructores
    public Odontologo() {
        this.activo = true;
        this.fechaContratacion = LocalDate.now();
    }

    public Odontologo(String nombre, String numeroTelefono, String correo, Especialidad especialidad,
                      String numeroLicencia, String horarioTrabajo, String antiguedad,
                      String colegiado, User usuario) {
        this();
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.correo = correo;
        this.especialidad = especialidad;
        this.numeroLicencia = numeroLicencia;
        this.horarioTrabajo = horarioTrabajo;
        this.antiguedad = antiguedad;
        this.colegiado = colegiado;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public String getHorarioTrabajo() { return horarioTrabajo; }
    public void setHorarioTrabajo(String horarioTrabajo) { this.horarioTrabajo = horarioTrabajo; }

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getAntiguedad() { return antiguedad; }
    public void setAntiguedad(String antiguedad) { this.antiguedad = antiguedad; }

    public String getColegiado() { return colegiado; }
    public void setColegiado(String colegiado) { this.colegiado = colegiado; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return nombre + " - " + especialidad;
    }
}