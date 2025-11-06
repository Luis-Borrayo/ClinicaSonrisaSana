package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import com.luisborrayo.clinicasonrisasana.services.PacienteService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("pacientesBean")
@ViewScoped
public class PacientesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PacientesBean.class.getName());

    @Inject
    private PacienteService pacienteService;

    @Inject
    private OdontologoService odontologoService;

    @Inject
    private Validator validator;

    private List<Pacientes> pacientes;
    private Pacientes selected;
    private boolean dialogVisible;

    private List<Odontologo> odontologos;

    private String criterioBusqueda;

    @PostConstruct
    public void init() {
        selected = new Pacientes();
        dialogVisible = false;
        cargarPacientes();
        cargarOdontologos();
    }

    public List<Pacientes> getPacientes() {
        if (pacientes == null) {
            cargarPacientes();
        }
        return pacientes;
    }

    public void cargarPacientes() {
        try {
            pacientes = pacienteService.obtenerTodosLosPacientes();
            if (pacientes == null) {
                pacientes = new ArrayList<>();
            }
            LOGGER.log(Level.INFO, "Pacientes cargados: {0}", pacientes.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar pacientes", e);
            pacientes = new ArrayList<>();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los pacientes");
        }
    }

    public void cargarOdontologos() {
        try {
            odontologos = odontologoService.obtenerTodosLosOdontologos();
            if (odontologos == null) {
                odontologos = new ArrayList<>();
            }
            LOGGER.log(Level.INFO, "Odontólogos cargados: {0}", odontologos.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar odontólogos", e);
            odontologos = new ArrayList<>();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los odontólogos");
        }
    }

    public void newPaciente() {
        clearFacesMessages();
        selected = new Pacientes();
        dialogVisible = true;
    }

    public void edit(Pacientes p) {
        clearFacesMessages();
        this.selected = p;
        dialogVisible = true;
    }

    public void save() {
        LOGGER.log(Level.INFO, "=== GUARDANDO PACIENTE ===");

        // Validación manual adicional
        Set<ConstraintViolation<Pacientes>> violations = validator.validate(selected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Pacientes> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                FacesContext.getCurrentInstance().addMessage("frmPacientes:msgPaciente",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        // Validación adicional
        if (selected.getOdontologo() == null) {
            FacesContext.getCurrentInstance().addMessage("frmPacientes:msgPaciente",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un odontólogo", null));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (selected.getSeguro() == null) {
            FacesContext.getCurrentInstance().addMessage("frmPacientes:msgPaciente",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un tipo de seguro", null));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            pacienteService.save(selected);
            dialogVisible = false;
            cargarPacientes();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Paciente guardado", "Operación exitosa"));
            selected = new Pacientes();
            LOGGER.log(Level.INFO, "✅ Paciente guardado exitosamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al guardar paciente", e);
            FacesContext.getCurrentInstance().addMessage("frmPacientes:msgPaciente",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", e.getMessage()));
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void delete(Long p) {
        try {
            pacienteService.delete(p);
            cargarPacientes();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Paciente eliminado", null));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar paciente", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el paciente: " + e.getMessage());
        }
    }

    public void buscarPacientes() {
        try {
            if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
                cargarPacientes();
            } else {
                pacientes = pacienteService.buscarPorNombre(criterioBusqueda);
                if (pacientes.isEmpty()) {
                    addMessage(FacesMessage.SEVERITY_WARN, "Info", "No se encontraron pacientes con ese criterio");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en búsqueda", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error en la búsqueda");
        }
    }

    private void clearFacesMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) return;
        for (Iterator<FacesMessage> it = ctx.getMessages(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
    }

    private String getFieldLabel(String fieldName) {
        Map<String, String> labels = new HashMap<>();
        labels.put("dpi", "DPI");
        labels.put("nombre", "Nombre");
        labels.put("apellido", "Apellido");
        labels.put("fechaNacimiento", "Fecha de nacimiento");
        labels.put("contacto", "Teléfono");
        labels.put("correo", "Correo");
        labels.put("direccion", "Dirección");
        labels.put("odontologo", "Odontólogo");
        labels.put("seguro", "Seguro");

        return labels.getOrDefault(fieldName, fieldName);
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters para los enums
    public Facturas.Seguro[] getSeguros() {
        return Facturas.Seguro.values();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public void setPacientes(List<Pacientes> pacientes) {
        this.pacientes = pacientes;
    }

    public Pacientes getSelected() {
        return selected;
    }

    public void setSelected(Pacientes selected) {
        this.selected = selected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

    public List<Odontologo> getOdontologos() {
        return odontologos;
    }

    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }

    public void setCriterioBusqueda(String criterioBusqueda) {
        this.criterioBusqueda = criterioBusqueda;
    }
}