package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
public class Facturas {

    public enum Seguro {
        INTEGRO,
        SEGUROS_GYT,
        SEGUROS_EL_ROBLE,
        MAPFRE,
        ASEGURADORA_GENERAL,
        CORPORACION_BI
    }

    public enum EstadoPago {
        PENDIENTE,
        CANCELADO,
        ADELANTO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Seguro seguro;

    @NotNull
    @Column(name = "pagos_parciales", nullable = false, precision = 10, scale = 2)
    private BigDecimal pagosParciales;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago;

    @NotNull
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    public Facturas() {}

    public Facturas(Paciente paciente, Odontologo odontologo, Cita cita, Tratamiento tratamiento,
                   BigDecimal subtotal, BigDecimal descuento, Seguro seguro,
                   BigDecimal pagosParciales, BigDecimal total, EstadoPago estadoPago, LocalDateTime fechaEmision) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.cita = cita;
        this.tratamiento = tratamiento;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.seguro = seguro;
        this.pagosParciales = pagosParciales;
        this.total = total;
        this.estadoPago = estadoPago;
        this.fechaEmision = fechaEmision;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }

    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }

    public Tratamiento getTratamiento() { return tratamiento; }
    public void setTratamiento(Tratamiento tratamiento) { this.tratamiento = tratamiento; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public Seguro getSeguro() { return seguro; }
    public void setSeguro(Seguro seguro) { this.seguro = seguro; }

    public BigDecimal getPagosParciales() { return pagosParciales; }
    public void setPagosParciales(BigDecimal pagosParciales) { this.pagosParciales = pagosParciales; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", paciente=" + paciente +
                ", odontologo=" + odontologo +
                ", cita=" + cita +
                ", tratamiento=" + tratamiento +
                ", subtotal=" + subtotal +
                ", descuento=" + descuento +
                ", seguro=" + seguro +
                ", pagosParciales=" + pagosParciales +
                ", total=" + total +
                ", estadoPago=" + estadoPago +
                ", fechaEmision=" + fechaEmision +
                '}';
    }
}