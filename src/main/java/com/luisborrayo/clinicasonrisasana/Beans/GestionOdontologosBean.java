package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import com.luisborrayo.clinicasonrisasana.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("gestionOdontologosBean")
@ViewScoped
public class GestionOdontologosBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GestionOdontologosBean.class.getName());

    @Inject
    private OdontologoService odontologoService;

    @Inject
    private UserService userService;

    @Inject
    private Validator validator;

    private List<Odontologo> odontologos;
    private Odontologo odontologoSeleccionado;
    private Odontologo nuevoOdontologo;

    // Campos del usuario relacionado
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String usuario;
    private String password;
    private String confirmPassword;

    // Campos específicos del odontólogo
    private String colegiado;
    private Odontologo.Especialidad especialidad;
    private String horario;
    private LocalDate fechaIngreso;
    private Integer anosExperiencia;
    private Boolean activo;
    private String criterioBusqueda;

    @PostConstruct
    public void init() {
        LOGGER.log(Level.INFO, "=== INICIALIZANDO GESTION ODONTOLOGOS BEAN ===");
        nuevoOdontologo = new Odontologo();
        odontologoSeleccionado = new Odontologo();
        activo = true;
        fechaIngreso = LocalDate.now();
        cargarOdontologos();
    }

    // ==================== CRUD METHODS ====================

    public void guardarOdontologo() {
        LOGGER.log(Level.INFO, "=== GUARDANDO ODONTOLOGO ===");
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            // Validar campos obligatorios
            if (!validarCamposObligatorios()) {
                return;
            }

            // Validar contraseñas si es nuevo
            if (nuevoOdontologo.getId() == null) {
                if (!validarPasswords()) {
                    return;
                }
            }

            // Verificar si el colegiado ya existe
            if (nuevoOdontologo.getId() == null) {
                Odontologo existeColegiado = odontologoService.obtenerPorColegiado(colegiado);
                if (existeColegiado != null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "El número de colegiado " + colegiado + " ya está registrado");
                    return;
                }
            }

            // Crear o actualizar usuario
            User user;
            if (nuevoOdontologo.getId() == null) {
                // Nuevo odontólogo - crear usuario
                user = new User();
                user.setNombres(nombres);
                user.setApellidos(apellidos);
                user.setCorreo(correo);
                user.setUsuario(usuario);
                user.setPassword(password);
                user.setRole(User.Role.ODONTOLOGO);
                user.setActive(activo);

                // Verificar si el usuario ya existe
                User existeUsuario = userService.buscarPorUsuario(usuario);
                if (existeUsuario != null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "El nombre de usuario ya existe");
                    return;
                }

                User existeCorreo = userService.buscarPorCorreo(correo);
                if (existeCorreo != null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "El correo ya está registrado");
                    return;
                }

                user = userService.guardar(user);
            } else {
                // Editar - actualizar usuario existente
                user = nuevoOdontologo.getUsuario();
                user.setNombres(nombres);
                user.setApellidos(apellidos);
                user.setCorreo(correo);
                user.setActive(activo);

                // Actualizar contraseña solo si se proporcionó una nueva
                if (password != null && !password.trim().isEmpty()) {
                    if (!validarPasswords()) {
                        return;
                    }
                    user.setPassword(password);
                }

                user = userService.guardar(user);
            }

            // Configurar odontólogo
            nuevoOdontologo.setUsuario(user);
            nuevoOdontologo.setColegiado(colegiado);
            nuevoOdontologo.setEspecialidad(especialidad);
            nuevoOdontologo.setAntiguedad(anosExperiencia != null ? anosExperiencia.toString() : "0");

            // Validar con Bean Validation
            if (!validarOdontologo(nuevoOdontologo)) {
                return;
            }

            // Guardar odontólogo
            Odontologo guardado = odontologoService.guardarOdontologo(nuevoOdontologo);

            if (guardado == null || guardado.getId() == null) {
                throw new RuntimeException("El odontólogo no se guardó correctamente");
            }

            LOGGER.log(Level.INFO, "✅ Odontólogo guardado: {0}", guardado.getNombreCompleto());

            // Limpiar formulario
            limpiarFormulario();
            cargarOdontologos();

            addMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                    "Odontólogo " + guardado.getNombreCompleto() + " guardado correctamente");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al guardar odontólogo", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "No se pudo guardar el odontólogo: " + e.getMessage());
        }
    }

    public void editarOdontologo() {
        LOGGER.log(Level.INFO, "=== EDITANDO ODONTOLOGO ===");
        try {
            if (odontologoSeleccionado != null && odontologoSeleccionado.getId() != null) {
                this.nuevoOdontologo = odontologoSeleccionado;

                // Cargar datos del usuario
                if (odontologoSeleccionado.getUsuario() != null) {
                    this.nombres = odontologoSeleccionado.getUsuario().getNombres();
                    this.apellidos = odontologoSeleccionado.getUsuario().getApellidos();
                    this.correo = odontologoSeleccionado.getUsuario().getCorreo();
                    this.usuario = odontologoSeleccionado.getUsuario().getUsuario();
                    this.activo = odontologoSeleccionado.getUsuario().isActive();
                }

                // Cargar datos del odontólogo
                this.colegiado = odontologoSeleccionado.getColegiado();
                this.especialidad = odontologoSeleccionado.getEspecialidad();

                try {
                    this.anosExperiencia = Integer.parseInt(odontologoSeleccionado.getAntiguedad());
                } catch (NumberFormatException e) {
                    this.anosExperiencia = 0;
                }

                // Limpiar contraseñas
                this.password = "";
                this.confirmPassword = "";

                addMessage(FacesMessage.SEVERITY_INFO, "Edición",
                        "Editando odontólogo: " + odontologoSeleccionado.getNombreCompleto());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al editar", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "No se pudo cargar el odontólogo: " + e.getMessage());
        }
    }

    public void eliminarOdontologo() {
        LOGGER.log(Level.INFO, "=== ELIMINANDO ODONTOLOGO ===");
        try {
            if (odontologoSeleccionado != null && odontologoSeleccionado.getId() != null) {
                String nombreCompleto = odontologoSeleccionado.getNombreCompleto();

                odontologoService.eliminarOdontologo(odontologoSeleccionado.getId());
                cargarOdontologos();

                addMessage(FacesMessage.SEVERITY_INFO, "Eliminado",
                        "Odontólogo " + nombreCompleto + " eliminado correctamente");

                odontologoSeleccionado = new Odontologo();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al eliminar", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "No se pudo eliminar el odontólogo: " + e.getMessage());
        }
    }

    public void cancelarEdicion() {
        limpiarFormulario();
        addMessage(FacesMessage.SEVERITY_INFO, "Cancelado", "Edición cancelada");
    }

    public void buscarOdontologos() {
        LOGGER.log(Level.INFO, "=== BÚSQUEDA DE ODONTOLOGOS ===");
        try {
            if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
                cargarOdontologos();
            } else {
                // Aquí puedes implementar búsqueda personalizada
                cargarOdontologos();
                // Filtrar por criterio si es necesario
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error en búsqueda", e);
        }
    }

    // ==================== VALIDATION METHODS ====================

    private boolean validarCamposObligatorios() {
        if (nombres == null || nombres.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es obligatorio");
            return false;
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El apellido es obligatorio");
            return false;
        }
        if (correo == null || correo.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo es obligatorio");
            return false;
        }
        if (colegiado == null || colegiado.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El número de colegiado es obligatorio");
            return false;
        }
        if (especialidad == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "La especialidad es obligatoria");
            return false;
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El usuario es obligatorio");
            return false;
        }
        return true;
    }

    private boolean validarPasswords() {
        if (password == null || password.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña es obligatoria");
            return false;
        }
        if (password.length() < 6) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    private boolean validarOdontologo(Odontologo odontologo) {
        Set<ConstraintViolation<Odontologo>> violations = validator.validate(odontologo);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Odontologo> v : violations) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Validación",
                        v.getPropertyPath() + ": " + v.getMessage());
            }
            return false;
        }
        return true;
    }

    // ==================== HELPER METHODS ====================

    private void cargarOdontologos() {
        try {
            odontologos = odontologoService.obtenerTodosLosOdontologos();
            if (odontologos == null) {
                odontologos = new ArrayList<>();
            }
            LOGGER.log(Level.INFO, "✅ Odontólogos cargados: {0}", odontologos.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al cargar odontólogos", e);
            odontologos = new ArrayList<>();
        }
    }

    private void limpiarFormulario() {
        nuevoOdontologo = new Odontologo();
        odontologoSeleccionado = new Odontologo();
        nombres = null;
        apellidos = null;
        correo = null;
        telefono = null;
        usuario = null;
        password = null;
        confirmPassword = null;
        colegiado = null;
        especialidad = null;
        horario = null;
        fechaIngreso = LocalDate.now();
        anosExperiencia = null;
        activo = true;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // ==================== GETTERS & SETTERS ====================

    public List<Odontologo> getOdontologos() {
        if (odontologos == null) {
            cargarOdontologos();
        }
        return odontologos;
    }

    public Odontologo.Especialidad[] getEspecialidades() {
        return Odontologo.Especialidad.values();
    }

    // Getters y Setters generados...
    public Odontologo getOdontologoSeleccionado() { return odontologoSeleccionado; }
    public void setOdontologoSeleccionado(Odontologo o) { this.odontologoSeleccionado = o; }

    public Odontologo getNuevoOdontologo() { return nuevoOdontologo; }
    public void setNuevoOdontologo(Odontologo o) { this.nuevoOdontologo = o; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getColegiado() { return colegiado; }
    public void setColegiado(String colegiado) { this.colegiado = colegiado; }

    public Odontologo.Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Odontologo.Especialidad especialidad) { this.especialidad = especialidad; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Integer getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getCriterioBusqueda() { return criterioBusqueda; }
    public void setCriterioBusqueda(String criterioBusqueda) { this.criterioBusqueda = criterioBusqueda; }
}