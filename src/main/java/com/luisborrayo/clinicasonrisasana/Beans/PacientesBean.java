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
    private Pacientes nuevoPaciente;
    private Pacientes pacienteSeleccionado;
    private List<Odontologo> odontologos;
    private String criterioBusqueda;
    private Facturas.Seguro seguroSeleccionado;

    @PostConstruct
    public void init() {
        LOGGER.log(Level.INFO, "=== INICIALIZANDO PACIENTESBEAN ===");
        nuevoPaciente = new Pacientes();
        pacienteSeleccionado = new Pacientes();
        cargarPacientes();
        cargarOdontologos();
    }

    // ========== MÉTODOS DE NEGOCIO ==========

    public void guardarPaciente() {
        LOGGER.log(Level.INFO, "=== INICIANDO GUARDADO DE PACIENTE ===");
        LOGGER.log(Level.INFO, "Datos del paciente - Nombre: {0}, DPI: {1}",
                new Object[]{nuevoPaciente.getNombre(), nuevoPaciente.getDpi()});

        try {
            // Asignar seguro si se seleccionó
            if (seguroSeleccionado != null) {
                nuevoPaciente.setSeguro(seguroSeleccionado);
                LOGGER.log(Level.INFO, "Seguro asignado: {0}", seguroSeleccionado);
            }

            // Validaciones
            if (!validarPaciente(nuevoPaciente)) {
                LOGGER.log(Level.WARNING, "Validación fallida para el paciente");
                return;
            }

            // Verificar si el DPI ya existe (para nuevos pacientes)
            if (nuevoPaciente.getId() == null && pacienteService.existeDpi(nuevoPaciente.getDpi())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El DPI " + nuevoPaciente.getDpi() + " ya está registrado"));
                LOGGER.log(Level.WARNING, "DPI ya existe: {0}", nuevoPaciente.getDpi());
                return;
            }

            // Verificar DPI para edición (excluyendo el paciente actual)
            if (nuevoPaciente.getId() != null &&
                    pacienteService.existeDpiExcluyendo(nuevoPaciente.getDpi(), nuevoPaciente.getId())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El DPI " + nuevoPaciente.getDpi() + " ya está registrado en otro paciente"));
                LOGGER.log(Level.WARNING, "DPI duplicado en edición: {0}", nuevoPaciente.getDpi());
                return;
            }

            // Guardar paciente
            Pacientes pacienteGuardado = pacienteService.save(nuevoPaciente);

            // Limpiar formulario
            limpiarFormulario();

            // Recargar lista
            cargarPacientes();

            // Mensaje de éxito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Paciente " + pacienteGuardado.getNombreCompleto() + " guardado correctamente"));

            LOGGER.log(Level.INFO, "✅ Paciente guardado exitosamente - ID: {0}, Nombre: {1}",
                    new Object[]{pacienteGuardado.getId(), pacienteGuardado.getNombreCompleto()});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al guardar paciente", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo guardar el paciente: " + e.getMessage()));
        }
    }

    public void editarPaciente() {
        LOGGER.log(Level.INFO, "=== INICIANDO EDICIÓN DE PACIENTE ===");
        try {
            if (pacienteSeleccionado != null && pacienteSeleccionado.getId() != null) {
                this.nuevoPaciente = pacienteSeleccionado;

                // Asignar seguro
                this.seguroSeleccionado = pacienteSeleccionado.getSeguro();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Edición",
                                "Editando paciente: " + pacienteSeleccionado.getNombreCompleto()));

                LOGGER.log(Level.INFO, "✅ Preparado para editar paciente: {0}", pacienteSeleccionado.getNombreCompleto());
                LOGGER.log(Level.INFO, "Odontólogo asignado: {0}",
                        pacienteSeleccionado.getOdontologo() != null ?
                                pacienteSeleccionado.getOdontologo().getNombre() : "Ninguno");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia",
                                "No se ha seleccionado ningún paciente para editar"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al preparar edición", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo cargar el paciente para edición: " + e.getMessage()));
        }
    }

    public void eliminarPaciente() {
        LOGGER.log(Level.INFO, "=== INICIANDO ELIMINACIÓN DE PACIENTE ===");
        try {
            if (pacienteSeleccionado != null && pacienteSeleccionado.getId() != null) {
                String nombrePaciente = pacienteSeleccionado.getNombreCompleto();
                pacienteService.delete(pacienteSeleccionado.getId());
                cargarPacientes();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminado",
                                "Paciente " + nombrePaciente + " eliminado correctamente"));

                pacienteSeleccionado = new Pacientes();

                LOGGER.log(Level.INFO, "✅ Paciente eliminado: {0}", nombrePaciente);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia",
                                "No se ha seleccionado ningún paciente para eliminar"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al eliminar paciente", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudo eliminar el paciente: " + e.getMessage()));
        }
    }

    public void cancelarEdicion() {
        LOGGER.log(Level.INFO, "=== CANCELANDO EDICIÓN ===");
        limpiarFormulario();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancelado", "Edición cancelada"));
    }

    public void buscarPacientes() {
        LOGGER.log(Level.INFO, "=== INICIANDO BÚSQUEDA ===");
        try {
            if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
                cargarPacientes();
                LOGGER.log(Level.INFO, "Búsqueda vacía - Mostrando todos los pacientes");
            } else {
                String criterio = criterioBusqueda.trim();
                pacientes = pacienteService.buscarPorNombre(criterio);
                if (pacientes.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Búsqueda",
                                    "No se encontraron pacientes con: '" + criterio + "'"));
                    LOGGER.log(Level.INFO, "Búsqueda sin resultados: {0}", criterio);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Búsqueda",
                                    "Se encontraron " + pacientes.size() + " pacientes"));
                    LOGGER.log(Level.INFO, "Búsqueda exitosa - Encontrados: {0} pacientes", pacientes.size());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error en búsqueda", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error en la búsqueda: " + e.getMessage()));
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private boolean validarPaciente(Pacientes paciente) {
        LOGGER.log(Level.INFO, "=== VALIDANDO PACIENTE ===");

        // Validación con Bean Validation
        Set<ConstraintViolation<Pacientes>> violations = validator.validate(paciente);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Pacientes> violation : violations) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Validación", violation.getMessage()));
                LOGGER.log(Level.WARNING, "Violación de validación: {0}", violation.getMessage());
            }
            return false;
        }

        // Validaciones adicionales
        if (paciente.getOdontologo() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación",
                            "Debe seleccionar un odontólogo"));
            LOGGER.log(Level.WARNING, "Validación fallida: Odontólogo no seleccionado");
            return false;
        }

        if (paciente.getSeguro() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación",
                            "Debe seleccionar un tipo de seguro"));
            LOGGER.log(Level.WARNING, "Validación fallida: Seguro no seleccionado");
            return false;
        }

        LOGGER.log(Level.INFO, "✅ Validación exitosa");
        return true;
    }

    private void limpiarFormulario() {
        LOGGER.log(Level.INFO, "Limpiando formulario");
        nuevoPaciente = new Pacientes();
        pacienteSeleccionado = new Pacientes();
        seguroSeleccionado = null;
    }

    public void cargarPacientes() {
        try {
            pacientes = pacienteService.obtenerTodosLosPacientes();
            if (pacientes == null) {
                pacientes = new ArrayList<>();
                LOGGER.warning("La lista de pacientes es null, inicializando lista vacía");
            }
            LOGGER.log(Level.INFO, "✅ Pacientes cargados: {0}", pacientes.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cargar pacientes", e);
            pacientes = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudieron cargar los pacientes: " + e.getMessage()));
        }
    }

    public void cargarOdontologos() {
        try {
            odontologos = odontologoService.obtenerTodosLosOdontologos();
            if (odontologos == null) {
                odontologos = new ArrayList<>();
                LOGGER.warning("La lista de odontólogos es null, inicializando lista vacía");
            }
            LOGGER.log(Level.INFO, "✅ Odontólogos cargados: {0}", odontologos.size());

            // DEBUG: Mostrar odontólogos en consola
            for (Odontologo odonto : odontologos) {
                LOGGER.log(Level.INFO, "   - {0} ({1}) - Activo: {2}",
                        new Object[]{
                                odonto.getNombre(),
                                odonto.getEspecialidad(),
                                odonto.getActivo()
                        });
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cargar odontólogos", e);
            odontologos = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudieron cargar los odontólogos: " + e.getMessage()));
        }
    }

    // ========== GETTERS Y SETTERS ==========

    public List<Pacientes> getPacientes() {
        if (pacientes == null) {
            cargarPacientes();
        }
        return pacientes;
    }

    public void setPacientes(List<Pacientes> pacientes) {
        this.pacientes = pacientes;
    }

    public Pacientes getNuevoPaciente() {
        if (nuevoPaciente == null) {
            nuevoPaciente = new Pacientes();
        }
        return nuevoPaciente;
    }

    public void setNuevoPaciente(Pacientes nuevoPaciente) {
        this.nuevoPaciente = nuevoPaciente;
    }

    public Pacientes getPacienteSeleccionado() {
        if (pacienteSeleccionado == null) {
            pacienteSeleccionado = new Pacientes();
        }
        return pacienteSeleccionado;
    }

    public void setPacienteSeleccionado(Pacientes pacienteSeleccionado) {
        this.pacienteSeleccionado = pacienteSeleccionado;
    }

    public List<Odontologo> getOdontologos() {
        if (odontologos == null) {
            cargarOdontologos();
        }
        return odontologos;
    }

    public void setOdontologos(List<Odontologo> odontologos) {
        this.odontologos = odontologos;
    }

    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }

    public void setCriterioBusqueda(String criterioBusqueda) {
        this.criterioBusqueda = criterioBusqueda;
    }

    public Facturas.Seguro getSeguroSeleccionado() {
        return seguroSeleccionado;
    }

    public void setSeguroSeleccionado(Facturas.Seguro seguroSeleccionado) {
        this.seguroSeleccionado = seguroSeleccionado;
    }

    public Facturas.Seguro[] getSeguros() {
        return Facturas.Seguro.values();
    }
}