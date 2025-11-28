package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.*;
import com.luisborrayo.clinicasonrisasana.services.FacturaService;
import com.luisborrayo.clinicasonrisasana.services.PacienteService;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import com.luisborrayo.clinicasonrisasana.services.CitasService;
import com.luisborrayo.clinicasonrisasana.services.TratamientoService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Named("facturasBean")
@ViewScoped
public class FacturasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacturaService facturaService;

    @Inject
    private PacienteService pacienteService;

    @Inject
    private OdontologoService odontologoService;

    @Inject
    private CitasService citasService;

    @Inject
    private TratamientoService tratamientoService;

    // Form fields (IDs for selects)
    private Long pacienteId;
    private Long odontologoId;
    private Long citaId;
    private Long tratamientoId;

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal pagosParciales = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private Facturas.Seguro seguroSeleccionado;
    private Facturas.EstadoPago estadoPagoSeleccionado;
    private LocalDateTime fechaEmision = LocalDateTime.now();

    // Lists for selects
    private List<Pacientes> pacientes;
    private List<Odontologo> odontologos;
    private List<Citas> citas;
    private List<Tratamiento> tratamientos;
    private List<Facturas> facturas;

    @PostConstruct
    public void init() {
        pacientes = pacienteService.obtenerTodosLosPacientes();
        odontologos = odontologoService.obtenerTodosLosOdontologos();
        citas = citasService.obtenerTodasLasCitas();
        tratamientos = tratamientoService.obtenerTodosLosTratamientos();
        facturas = facturaService.findAll();
    }

    // Acciones
    public void calcularTotal() {
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (descuento == null) descuento = BigDecimal.ZERO;
        if (pagosParciales == null) pagosParciales = BigDecimal.ZERO;

        // total = subtotal - descuento  (pagosParciales se registra aparte)
        total = subtotal.subtract(descuento);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
    }

    public void guardarFactura() {
        try {
            // Validaciones simples
            if (pacienteId == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un paciente"));
                return;
            }
            if (odontologoId == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un odontólogo"));
                return;
            }
            if (tratamientoId == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un tratamiento"));
                return;
            }
            if (seguroSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un seguro"));
                return;
            }
            if (estadoPagoSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione estado de pago"));
                return;
            }

            // Buscar entidades
            Pacientes paciente = pacienteService.obtenerPacientePorId(pacienteId);
            Odontologo odontologo = odontologoService.obtenerOdontologoPorId(odontologoId);
            Citas cita = null;
            if (citaId != null) cita = citasService.findById(citaId);
            Tratamiento tratamiento = tratamientoService.findById(tratamientoId);

            // recalcular total por seguridad
            calcularTotal();

            Facturas factura = new Facturas();
            factura.setPaciente(paciente);
            factura.setOdontologo(odontologo);
            factura.setCita(cita);
            factura.setTratamiento(tratamiento);
            factura.setSubtotal(subtotal);
            factura.setDescuento(descuento);
            factura.setPagosParciales(pagosParciales);
            factura.setTotal(total);
            factura.setSeguro(seguroSeleccionado);
            factura.setEstadoPago(estadoPagoSeleccionado);
            factura.setFechaEmision(fechaEmision != null ? fechaEmision : LocalDateTime.now());

            Facturas saved = facturaService.save(factura);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Factura guardada (ID: " + saved.getId() + ")"));

            // refrescar lista y limpiar formulario
            facturas = facturaService.findAll();
            limpiarFormulario();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage()));
        }
    }

    public void limpiarFormulario() {
        pacienteId = null;
        odontologoId = null;
        citaId = null;
        tratamientoId = null;
        subtotal = BigDecimal.ZERO;
        descuento = BigDecimal.ZERO;
        pagosParciales = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        seguroSeleccionado = null;
        estadoPagoSeleccionado = null;
        fechaEmision = LocalDateTime.now();
    }

    // Getters / Setters (omito algunos por brevedad — incluye todos en tu clase)
    public List<Pacientes> getPacientes() { return pacientes; }
    public List<Odontologo> getOdontologos() { return odontologos; }
    public List<Citas> getCitas() { return citas; }
    public List<Tratamiento> getTratamientos() { return tratamientos; }
    public List<Facturas> getFacturas() { return facturas; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getOdontologoId() { return odontologoId; }
    public void setOdontologoId(Long odontologoId) { this.odontologoId = odontologoId; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public Long getTratamientoId() { return tratamientoId; }
    public void setTratamientoId(Long tratamientoId) { this.tratamientoId = tratamientoId; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getPagosParciales() { return pagosParciales; }
    public void setPagosParciales(BigDecimal pagosParciales) { this.pagosParciales = pagosParciales; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Facturas.Seguro getSeguroSeleccionado() { return seguroSeleccionado; }
    public void setSeguroSeleccionado(Facturas.Seguro seguroSeleccionado) { this.seguroSeleccionado = seguroSeleccionado; }
    public Facturas.EstadoPago getEstadoPagoSeleccionado() { return estadoPagoSeleccionado; }
    public void setEstadoPagoSeleccionado(Facturas.EstadoPago estadoPagoSeleccionado) { this.estadoPagoSeleccionado = estadoPagoSeleccionado; }

    public Facturas.Seguro[] getSeguros() { return Facturas.Seguro.values(); }
    public Facturas.EstadoPago[] getEstadosPago() { return Facturas.EstadoPago.values(); }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
