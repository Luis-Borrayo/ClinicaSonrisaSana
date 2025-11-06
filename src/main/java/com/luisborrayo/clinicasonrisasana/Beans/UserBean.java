package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.User;
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
import java.util.*;

/**
 * Bean para la administración de usuarios (CRUD)
 * Usado en la página de gestión de usuarios
 */
@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    @Inject
    private Validator validator;

    private User selected;
    private boolean dialogVisible;
    private String confirmPassword;

    @PostConstruct
    public void init() {
        selected = new User();
        dialogVisible = false;
        confirmPassword = "";
    }

    /**
     * Obtener lista de todos los usuarios
     */
    public List<User> getList() {
        try {
            return userService.listar();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al cargar usuarios", e.getMessage()));
            return new ArrayList<>();
        }
    }

    /**
     * Obtener solo usuarios activos
     */
    public List<User> getListActivos() {
        try {
            return userService.listarActivos();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Preparar formulario para nuevo usuario
     */
    public void newUser() {
        clearFacesMessages();
        selected = new User();
        selected.setActive(true);
        confirmPassword = "";
        dialogVisible = true;
    }

    /**
     * Editar usuario existente
     */
    public void edit(User u) {
        clearFacesMessages();
        this.selected = u;
        this.confirmPassword = u.getPassword();
        dialogVisible = true;
    }

    /**
     * Guardar usuario (crear o actualizar)
     */
    public void save() {
        // Validar confirmación de contraseña
        if (!selected.getPassword().equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage("frmUsers:msgUser",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Las contraseñas no coinciden"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        // Validar con Bean Validation
        Set<ConstraintViolation<User>> violations = validator.validate(selected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<User> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                FacesContext.getCurrentInstance().addMessage("frmUsers:msgUser",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            // Verificar si el usuario ya existe (solo para nuevos usuarios)
            if (selected.getId() == null) {
                User existeUsuario = userService.buscarPorUsuario(selected.getUsuario());
                if (existeUsuario != null) {
                    FacesContext.getCurrentInstance().addMessage("frmUsers:msgUser",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Error", "El nombre de usuario ya existe"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }

                User existeCorreo = userService.buscarPorCorreo(selected.getCorreo());
                if (existeCorreo != null) {
                    FacesContext.getCurrentInstance().addMessage("frmUsers:msgUser",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Error", "El correo ya está registrado"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
            }

            userService.guardar(selected);
            dialogVisible = false;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuario guardado", "Operación exitosa"));
            selected = new User();
            confirmPassword = "";
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al guardar usuario", e.getMessage()));
        }
    }

    /**
     * Eliminar usuario permanentemente
     */
    public void delete(User u) {
        try {
            userService.eliminar(u);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuario eliminado", "El usuario ha sido eliminado permanentemente"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al eliminar usuario", e.getMessage()));
        }
    }

    /**
     * Desactivar usuario (soft delete)
     */
    public void desactivar(User u) {
        try {
            userService.desactivar(u);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuario desactivado", "El usuario ha sido desactivado"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al desactivar usuario", e.getMessage()));
        }
    }

    /**
     * Activar usuario
     */
    public void activar(User u) {
        try {
            u.setActive(true);
            userService.guardar(u);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuario activado", "El usuario ha sido activado"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al activar usuario", e.getMessage()));
        }
    }

    /**
     * Limpiar mensajes de JSF
     */
    private void clearFacesMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) return;
        for (Iterator<FacesMessage> it = ctx.getMessages(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
    }

    /**
     * Obtener etiquetas amigables para los campos
     */
    private String getFieldLabel(String fieldName) {
        Map<String, String> labels = new HashMap<>();
        labels.put("correo", "Correo electrónico");
        labels.put("nombres", "Nombres");
        labels.put("apellidos", "Apellidos");
        labels.put("usuario", "Usuario");
        labels.put("password", "Contraseña");
        labels.put("role", "Rol");
        labels.put("active", "Activo");

        return labels.getOrDefault(fieldName, fieldName);
    }

    // ==================== GETTERS Y SETTERS ====================

    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Obtener todos los roles disponibles para dropdown
     */
    public User.Role[] getRoles() {
        return User.Role.values();
    }
}