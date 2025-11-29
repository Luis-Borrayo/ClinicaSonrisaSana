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
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class FacturasBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FacturasBean.class.getName());

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
        LOGGER.log(Level.INFO, "=== INICIALIZANDO FACTURASBEAN ===");

        try {
            facturas = facturasRepository.findAll();
            pacientes = pacientesRepository.findAll();
            odontologos = odontologoRepository.findAll();
            citas = citasRepository.findAll();
            tratamientos = tratamientoRepository.findAll();

            facturaActual = new Facturas();
            facturaActual.setFechaEmision(LocalDateTime.now());

            LOGGER.log(Level.INFO, "✅ Datos cargados - Facturas: {0}, Pacientes: {1}, Odontólogos: {2}",
                    new Object[]{facturas.size(), pacientes.size(), odontologos.size()});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al inicializar FacturasBean", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    /**
     * Calcula el total de la factura
     */
    public void calcularTotal() {
        try {
            BigDecimal subtotal = facturaActual.getSubtotal() != null ? facturaActual.getSubtotal() : BigDecimal.ZERO;
            BigDecimal descuento = facturaActual.getDescuento() != null ? facturaActual.getDescuento() : BigDecimal.ZERO;
            BigDecimal pagosParciales = facturaActual.getPagosParciales() != null ? facturaActual.getPagosParciales() : BigDecimal.ZERO;

            // Total = Subtotal - Descuento - PagosParciales
            BigDecimal total = subtotal.subtract(descuento).subtract(pagosParciales);

            // Asegurarse de que no sea negativo
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }

            facturaActual.setTotal(total);

            LOGGER.log(Level.INFO, "💰 Total calculado: Q{0}", total);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error calculando total", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo calcular el total");
        }
    }

    public void guardarFactura() {
        LOGGER.log(Level.INFO, "=== GUARDANDO FACTURA ===");

        try {
            // Validaciones
            if (facturaActual.getPaciente() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un paciente");
                return;
            }

            if (facturaActual.getOdontologo() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un odontólogo");
                return;
            }

            if (facturaActual.getCita() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar una cita");
                return;
            }

            if (facturaActual.getTratamiento() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un tratamiento");
                return;
            }

            // Calcular total antes de guardar
            calcularTotal();

            // Guardar
            facturasRepository.save(facturaActual);

            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Factura guardada correctamente");

            // Recargar lista y limpiar formulario
            facturas = facturasRepository.findAll();
            nuevaFactura();

            LOGGER.log(Level.INFO, "✅ Factura guardada - ID: {0}", facturaActual.getId());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al guardar factura", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la factura: " + e.getMessage());
        }
    }

    public void eliminarFactura() {
        try {
            if (facturaActual != null && facturaActual.getId() != null) {
                facturasRepository.delete(facturaActual.getId());
                facturas = facturasRepository.findAll();
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Factura eliminada correctamente");
                nuevaFactura();

                LOGGER.log(Level.INFO, "✅ Factura eliminada - ID: {0}", facturaActual.getId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al eliminar factura", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la factura");
        }
    }

    public void nuevaFactura() {
        facturaActual = new Facturas();
        facturaActual.setFechaEmision(LocalDateTime.now());
    }

    public void editarFactura(Facturas factura) {
        this.facturaActual = factura;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // ==================== GETTERS Y SETTERS ====================

    public List<Facturas> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<Facturas> facturas) {
        this.facturas = facturas;
    }

    public Facturas getFacturaActual() {
        return facturaActual;
    }

    public void setFacturaActual(Facturas facturaActual) {
        this.facturaActual = facturaActual;
    }

    public List<Pacientes> getPacientes() {
        return pacientes;
    }

    public List<Odontologo> getOdontologos() {
        return odontologos;
    }

    public List<Citas> getCitas() {
        return citas;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public Facturas.Seguro[] getSeguros() {
        return Facturas.Seguro.values();
    }

    public Facturas.EstadoPago[] getEstadosPago() {
        return Facturas.EstadoPago.values();
    }
}