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
    private List<Odontologo> odontologos;  // ✅ CORREGIDO: SIN ESPACIO
    private String criterioBusqueda;
    private Facturas.Seguro seguroSeleccionado;
    private Long odontologoId;

    @PostConstruct
    public void init() {
        LOGGER.log(Level.INFO, "=== INICIALIZANDO PACIENTESBEAN ===");
        nuevoPaciente = new Pacientes();
        pacienteSeleccionado = new Pacientes();
        cargarPacientes();
        cargarOdontologos();

        // DEBUG ADICIONAL
        LOGGER.log(Level.INFO, "Odontólogos después de init: {0}",
                odontologos != null ? odontologos.size() : "NULL");
    }

    // ========== MÉTODOS DE NEGOCIO ==========

    public void guardarPaciente() {
        LOGGER.log(Level.INFO, "=== INICIANDO GUARDADO DE PACIENTE ===");
        LOGGER.log(Level.INFO, "Datos del paciente - Nombre: {0}, DPI: {1}",
                new Object[]{nuevoPaciente.getNombre(), nuevoPaciente.getDpi()});

        try {
            LOGGER.log(Level.INFO, "DEBUG valores desde formulario - nombre: {0}, apellido: {1}, direccion: {2}, fechaNacimiento: {3}, contacto: {4}, dpi: {5}",
                    new Object[]{
                            nuevoPaciente != null ? nuevoPaciente.getNombre() : "NULL",
                            nuevoPaciente != null ? nuevoPaciente.getApellido() : "NULL",
                            nuevoPaciente != null ? nuevoPaciente.getDireccion() : "NULL",
                            nuevoPaciente != null ? nuevoPaciente.getFechaNacimiento() : "NULL",
                            nuevoPaciente != null ? nuevoPaciente.getContacto() : "NULL",
                            nuevoPaciente != null ? nuevoPaciente.getDpi() : "NULL"
                    });

            FacesContext ctx = FacesContext.getCurrentInstance();

            // Si algún campo crítico viene null, añade mensaje específico conectado al componente (clientId)
            boolean huboError = false;
            if (nuevoPaciente == null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los datos del paciente no llegaron al servidor."));
                LOGGER.severe("nuevoPaciente == null");
                return;
            }
            if (nuevoPaciente.getApellido() == null || nuevoPaciente.getApellido().trim().isEmpty()) {
                ctx.addMessage("mainForm:apellido", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación apellido", "no debe ser nulo"));
                LOGGER.warning("apellido nulo");
                huboError = true;
            }
            if (nuevoPaciente.getDireccion() == null || nuevoPaciente.getDireccion().trim().isEmpty()) {
                ctx.addMessage("mainForm:direccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación direccion", "no debe ser nulo"));
                LOGGER.warning("direccion nula");
                huboError = true;
            }
            if (nuevoPaciente.getFechaNacimiento() == null) {
                ctx.addMessage("mainForm:fechaNacimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación fechaNacimiento", "no debe ser nulo"));
                LOGGER.warning("fechaNacimiento nula");
                huboError = true;
            }

            if (huboError) {
                // no continuar con save, ya se mostraron mensajes por componente
                LOGGER.info("Se detiene guardado por validaciones iniciales (campos nulos)");
                return;
            }

            // ✅ VALIDACIÓN 1: Verificar que el odontólogo esté seleccionado
            if (odontologoId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Debe seleccionar un odontólogo"));
                LOGGER.log(Level.WARNING, "Validación fallida: Odontólogo no seleccionado");
                return;
            }

            // ✅ VALIDACIÓN 2: Verificar que el seguro esté seleccionado
            if (seguroSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Debe seleccionar un tipo de seguro"));
                LOGGER.log(Level.WARNING, "Validación fallida: Seguro no seleccionado");
                return;
            }

            // ✅ Asignar seguro (ya validado que no es null)
            nuevoPaciente.setSeguro(seguroSeleccionado);
            LOGGER.log(Level.INFO, "Seguro asignado: {0}", seguroSeleccionado);

            // ✅ Buscar y asignar Odontólogo (ya validado que no es null)
            Odontologo odontologo = odontologoService.obtenerOdontologoPorId(odontologoId);
            if (odontologo == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El odontólogo seleccionado no existe"));
                return;
            }

            nuevoPaciente.setOdontologo(odontologo);
            String nombreOdontologo = odontologo.getUsuario() != null ?
                    odontologo.getUsuario().getNombres() + " " + odontologo.getUsuario().getApellidos() :
                    "Odontólogo " + odontologo.getColegiado();
            LOGGER.log(Level.INFO, "Odontólogo asignado: {0}", nombreOdontologo);

            // ✅ Validaciones con Bean Validation
            if (!validarPaciente(nuevoPaciente)) {
                LOGGER.log(Level.WARNING, "Validación fallida para el paciente");
                return;
            }

            // ✅ Verificar si el DPI ya existe (para nuevos pacientes)
            if (nuevoPaciente.getId() == null && pacienteService.existeDpi(nuevoPaciente.getDpi())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El DPI " + nuevoPaciente.getDpi() + " ya está registrado"));
                LOGGER.log(Level.WARNING, "DPI ya existe: {0}", nuevoPaciente.getDpi());
                return;
            }

            // ✅ Verificar DPI para edición (excluyendo el paciente actual)
            if (nuevoPaciente.getId() != null &&
                    pacienteService.existeDpiExcluyendo(nuevoPaciente.getDpi(), nuevoPaciente.getId())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El DPI " + nuevoPaciente.getDpi() + " ya está registrado en otro paciente"));
                LOGGER.log(Level.WARNING, "DPI duplicado en edición: {0}", nuevoPaciente.getDpi());
                return;
            }

            // ✅ Guardar paciente
            LOGGER.log(Level.INFO, "💾 Intentando guardar paciente en la base de datos...");
            Pacientes pacienteGuardado = pacienteService.save(nuevoPaciente);

            if (pacienteGuardado == null || pacienteGuardado.getId() == null) {
                throw new RuntimeException("El paciente no se guardó correctamente");
            }

            LOGGER.log(Level.INFO, "✅ Paciente guardado exitosamente - ID: {0}, Nombre: {1}",
                    new Object[]{pacienteGuardado.getId(), pacienteGuardado.getNombreCompleto()});

            // ✅ Limpiar formulario
            limpiarFormulario();

            // ✅ Recargar lista
            cargarPacientes();

            // ✅ Mensaje de éxito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Paciente " + pacienteGuardado.getNombreCompleto() + " guardado correctamente con ID: " + pacienteGuardado.getId()));

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
                //Asigna odontologo
                if (pacienteSeleccionado.getOdontologo() != null) {
                    this.odontologoId = pacienteSeleccionado.getOdontologo().getId();
                }

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Edición",
                                "Editando paciente: " + pacienteSeleccionado.getNombreCompleto()));

                LOGGER.log(Level.INFO, "✅ Preparado para editar paciente: {0}", pacienteSeleccionado.getNombreCompleto());
                String odontologoInfo = pacienteSeleccionado.getOdontologo() != null ?
                        (pacienteSeleccionado.getOdontologo().getUsuario() != null ?
                                pacienteSeleccionado.getOdontologo().getUsuario().getNombres() + " " +
                                        pacienteSeleccionado.getOdontologo().getUsuario().getApellidos() :
                                "Colegiado: " + pacienteSeleccionado.getOdontologo().getColegiado()) :
                        "Ninguno";
                LOGGER.log(Level.INFO, "Odontólogo asignado: {0}", odontologoInfo);
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

        Set<ConstraintViolation<Pacientes>> violations = validator.validate(paciente);
        if (!violations.isEmpty()) {
            // Agrupar por propiedad para no spamear el usuario si hay muchas violaciones en la misma propiedad
            Map<String, StringBuilder> mensajesPorCampo = new LinkedHashMap<>();
            for (ConstraintViolation<Pacientes> v : violations) {
                String property = v.getPropertyPath().toString();
                String message = v.getMessage();

                mensajesPorCampo
                        .computeIfAbsent(property, k -> new StringBuilder())
                        .append(message).append("; ");

                LOGGER.log(Level.WARNING, "Violación: campo={0} mensaje={1}", new Object[]{property, message});
            }

            // Enviar un FacesMessage por cada campo (así aparecen en p:messages)
            for (Map.Entry<String, StringBuilder> e : mensajesPorCampo.entrySet()) {
                String campo = e.getKey();
                String texto = e.getValue().toString();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Validación " + campo, texto));
            }

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
        odontologoId = null;
    }

    public void cargarPacientes() {
        try {
            LOGGER.log(Level.INFO, "=== CARGANDO PACIENTES ===");
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
            LOGGER.log(Level.INFO, "=== INICIANDO CARGA DE ODONTÓLOGOS ===");

            // Verificar que el servicio no sea null
            if (odontologoService == null) {
                LOGGER.severe("❌ OdontologoService es NULL");
                odontologos = new ArrayList<>();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Crítico",
                                "El servicio de odontólogos no está inicializado"));
                return;
            }

            LOGGER.log(Level.INFO, "✅ OdontologoService está inicializado");

            // Intentar obtener los odontólogos
            odontologos = odontologoService.obtenerTodosLosOdontologos();

            if (odontologos == null) {
                LOGGER.warning("⚠️ La lista de odontólogos es null, inicializando lista vacía");
                odontologos = new ArrayList<>();
            } else {
                LOGGER.log(Level.INFO, "✅ Odontólogos cargados: {0}", odontologos.size());

                // DEBUG: Mostrar cada odontólogo
                if (odontologos.isEmpty()) {
                    LOGGER.warning("⚠️ No hay odontólogos en la base de datos");
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia",
                                    "No hay odontólogos registrados en el sistema"));
                } else {
                    for (Odontologo odonto : odontologos) {
                        LOGGER.log(Level.INFO, "   📋 ID: {0} - {1} ({2}) - Activo: {3}",
                                new Object[]{
                                        odonto.getId(),
                                        odonto.getNombreCompleto(),
                                        odonto.getEspecialidad(),
                                        odonto.getActivo()
                                });
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cargar odontólogos", e);
            e.printStackTrace();
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
        LOGGER.log(Level.INFO, "🔍 getOdontologos() llamado - Lista: {0}",
                odontologos != null ? odontologos.size() + " elementos" : "NULL");

        if (odontologos == null) {
            LOGGER.warning("⚠️ Lista de odontólogos es NULL, cargando...");
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

    public Long getOdontologoId() {
        return odontologoId;
    }

    public void setOdontologoId(Long odontologoId) {
        this.odontologoId = odontologoId;
    }
}