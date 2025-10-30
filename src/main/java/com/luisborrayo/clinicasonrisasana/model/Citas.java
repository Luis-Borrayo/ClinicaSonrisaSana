package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Citas {

    public enum Estados {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA,
        REPROGRAMADA,
        ATENDIDA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Pacientes paciente;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;

    @NotNull
    @Column(name = "fechacita", nullable = false)
    private LocalDateTime fechaCita;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estados estado;

    @NotNull
    @Lob
    @Column(name = "observaciones", nullable = false)
    private String observaciones;

    public Citas(){}

    public Citas(Pacientes paciente, Odontologo odontologo, Tratamiento tratamiento,
                 LocalDateTime fechaCita, Estados estado, String observaciones) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.tratamiento = tratamiento;
        this.fechaCita = fechaCita;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pacientes getPaciente() { return paciente; }
    public void setPaciente(Pacientes paciente) { this.paciente = paciente; }

    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }

    public Tratamiento getTratamiento() { return tratamiento; }
    public void setTratamiento(Tratamiento tratamiento) { this.tratamiento = tratamiento; }

    public LocalDateTime getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDateTime fechaCita) { this.fechaCita = fechaCita; }

    public Estados getEstado() { return estado; }
    public void setEstado(Estados estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

}
