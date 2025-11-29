package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.*;
import com.luisborrayo.clinicasonrisasana.repositories.*;
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

@Named
@ViewScoped
public class FacturasBean implements Serializable {

    @Inject
    private FacturasRepository facturasRepository;

    @Inject
    private PacientesRepository pacientesRepository;

    @Inject
    private OdontologoRepository odontologoRepository;

    @Inject
    private CitasRepository citasRepository;

    @Inject
    private TratamientoRepository tratamientoRepository;

    private List<Facturas> facturas;
    private List<Pacientes> pacientes;
    private List<Odontologo> odontologos;
    private List<Citas> citas;
    private List<Tratamiento> tratamientos;

    private Facturas facturaActual;

    @PostConstruct
    public void init() {
        facturas = facturasRepository.findAll();
        pacientes = pacientesRepository.findAll();
        odontologos = odontologoRepository.findAll();
        citas = citasRepository.findAll();
        tratamientos = tratamientoRepository.findAll();

        facturaActual = new Facturas();
        facturaActual.setFechaEmision(LocalDateTime.now());
    }

    public List<Facturas> getFacturas() { return facturas; }
    public Facturas getFacturaActual() { return facturaActual; }
    public List<Pacientes> getPacientes() { return pacientes; }
    public List<Odontologo> getOdontologos() { return odontologos; }
    public List<Citas> getCitas() { return citas; }
    public List<Tratamiento> getTratamientos() { return tratamientos; }

    public Facturas.Seguro[] getSeguros() {
        return Facturas.Seguro.values();
    }

    public Facturas.EstadoPago[] getEstadosPago() {
        return Facturas.EstadoPago.values();
    }

    public void nuevaFactura() {
        facturaActual = new Facturas();
        facturaActual.setFechaEmision(LocalDateTime.now());
    }

    public void editarFactura(Facturas factura) {
        this.facturaActual = factura;
    }

    public void guardarFactura() {
        try {
            facturasRepository.save(facturaActual);

            agregarMensaje("Factura guardada correctamente");

            facturas = facturasRepository.findAll();
            nuevaFactura();

        } catch (Exception e) {
            agregarMensaje("Error al guardar factura: " + e.getMessage());
        }
    }

    public void eliminarFactura(Facturas factura) {
        try {
            facturasRepository.delete(factura.getId());
            facturas = facturasRepository.findAll();
            agregarMensaje("Factura eliminada correctamente");
        } catch (Exception e) {
            agregarMensaje("No se pudo eliminar la factura");
        }
    }

    private void agregarMensaje(String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(texto));
    }
}
