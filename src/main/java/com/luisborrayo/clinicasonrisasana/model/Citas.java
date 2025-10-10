package com.luisborrayo.clinicasonrisasana.model;

import java.time.LocalDateTime;

public class Citas {
    private Long id;
    private Paciente paciente;
    private Odontologo odontologo;
    private Tratamiento tratamiento;
    private LocalDateTime fechaCita;
    private String estado;
    private String observaciones;

    // Constructores
    public Citas() {}

    public Citas(Paciente paciente, Odontologo odontologo, Tratamiento tratamiento,
                 LocalDateTime fechaCita, String estado, String observaciones) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.tratamiento = tratamiento;
        this.fechaCita = fechaCita;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }

    public Tratamiento getTratamiento() { return tratamiento; }
    public void setTratamiento(Tratamiento tratamiento) { this.tratamiento = tratamiento; }

    public LocalDateTime getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDateTime fechaCita) { this.fechaCita = fechaCita; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}