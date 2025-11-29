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
    private String passwordOriginal; // Para guardar la contraseña original al editar

    @PostConstruct
    public void init() {
        selected = new User();
        dialogVisible = false;
        confirmPassword = "";
        passwordOriginal = "";
    }

    /**
     * Obtener lista de todos los usuarios
     */
    public List<User> getList() {
        try {
            return userService.listar();
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar usuarios",
                    e.getMessage());
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
        passwordOriginal = "";
        dialogVisible = true;
    }

    /**
     * Editar usuario existente
     */
    public void edit(User u) {
        clearFacesMessages();

        // Hacer una copia del usuario para no modificar directamente el original
        this.selected = u;

        // Guardar la contraseña original
        this.passwordOriginal = u.getPassword();

        // Limpiar los campos de contraseña en el formulario
        this.selected.setPassword("");
        this.confirmPassword = "";

        dialogVisible = true;
    }

    /**
     * Guardar usuario (crear o actualizar)
     */
    public void save() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // Validación de contraseña
        boolean esNuevo = (selected.getId() == null);
        boolean cambioPassword = !selected.getPassword().isEmpty();

        // Si es nuevo usuario, la contraseña es obligatoria
        if (esNuevo && selected.getPassword().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "La contraseña es obligatoria para nuevos usuarios");
            ctx.validationFailed();
            return;
        }

        // Si se está cambiando la contraseña, validar confirmación
        if (cambioPassword) {
            if (confirmPassword == null || confirmPassword.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "Debe confirmar la contraseña");
                ctx.validationFailed();
                return;
            }

            if (!selected.getPassword().equals(confirmPassword)) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "Las contraseñas no coinciden");
                ctx.validationFailed();
                return;
            }

            if (selected.getPassword().length() < 6) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "La contraseña debe tener al menos 6 caracteres");
                ctx.validationFailed();
                return;
            }
        } else if (!esNuevo) {
            // Si no se está cambiando la contraseña, restaurar la original
            selected.setPassword(passwordOriginal);
        }

        // Validar con Bean Validation
        Set<ConstraintViolation<User>> violations = validator.validate(selected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<User> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                addMessage(FacesMessage.SEVERITY_ERROR,
                        label,
                        message);
            }

            ctx.validationFailed();
            return;
        }

        try {
            // Verificar si el usuario ya existe (solo para nuevos usuarios)
            if (esNuevo) {
                User existeUsuario = userService.buscarPorUsuario(selected.getUsuario());
                if (existeUsuario != null) {
                    addMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El nombre de usuario ya existe");
                    ctx.validationFailed();
                    return;
                }

                User existeCorreo = userService.buscarPorCorreo(selected.getCorreo());
                if (existeCorreo != null) {
                    addMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El correo ya está registrado");
                    ctx.validationFailed();
                    return;
                }
            } else {
                // Verificar que no se duplique el correo (excepto el mismo usuario)
                User existeCorreo = userService.buscarPorCorreo(selected.getCorreo());
                if (existeCorreo != null && !existeCorreo.getId().equals(selected.getId())) {
                    addMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El correo ya está registrado por otro usuario");
                    ctx.validationFailed();
                    return;
                }
            }

            // Guardar usuario
            userService.guardar(selected);

            dialogVisible = false;
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Éxito",
                    esNuevo ? "Usuario creado exitosamente" : "Usuario actualizado exitosamente");

            // Limpiar formulario
            selected = new User();
            confirmPassword = "";
            passwordOriginal = "";

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar usuario",
                    e.getMessage());
        }
    }

    /**
     * Eliminar usuario permanentemente
     */
    public void delete(User u) {
        try {
            userService.eliminar(u);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Usuario eliminado",
                    "El usuario ha sido eliminado permanentemente");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al eliminar usuario",
                    e.getMessage());
        }
    }

    /**
     * Desactivar usuario (soft delete)
     */
    public void desactivar(User u) {
        try {
            userService.desactivar(u);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Usuario desactivado",
                    "El usuario ha sido desactivado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al desactivar usuario",
                    e.getMessage());
        }
    }

    /**
     * Activar usuario
     */
    public void activar(User u) {
        try {
            u.setActive(true);
            userService.guardar(u);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Usuario activado",
                    "El usuario ha sido activado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al activar usuario",
                    e.getMessage());
        }
    }

    /**
     * Agregar mensaje al contexto de JSF
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    /**
     * Limpiar mensajes de JSF
     */
    private void clearFacesMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) return;

        Iterator<FacesMessage> it = ctx.getMessages();
        while (it.hasNext()) {
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

    // ==================== FUNCIONES ADMIN ADICIONALES ====================

    /**
     * Cambiar contraseña de un usuario
     */

    public void toggleEstado(User u) {
        if (u.isActive()) {
            desactivar(u);
        } else {
            activar(u);
        }
    }

    public void cambiarPassword(User u, String nuevaPass, String confirmPass) {
        clearFacesMessages();

        if (nuevaPass == null || nuevaPass.length() < 6) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "La nueva contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!nuevaPass.equals(confirmPass)) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "Las contraseñas no coinciden");
            return;
        }

        try {
            userService.cambiarContrasena(u, nuevaPass);

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Contraseña actualizada",
                    "La contraseña fue modificada exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cambiar la contraseña",
                    e.getMessage());
        }
    }

    /**
     * Cambiar rol del usuario sin abrir el diálogo de edición
     */
    public void cambiarRol(User u, User.Role nuevoRol) {
        clearFacesMessages();

        try {
            userService.asignarRol(u, nuevoRol);

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Rol actualizado",
                    "El usuario ahora es: " + nuevoRol);
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cambiar de rol",
                    e.getMessage());
        }
    }

    /**
     * Restablecer contraseña automáticamente (ADMIN)
     * Nueva contraseña por defecto: usuario123
     */
    public void resetPassword(User u) {
        clearFacesMessages();

        String nueva = u.getUsuario() + "123";

        try {
            userService.cambiarContrasena(u, nueva);

            addMessage(FacesMessage.SEVERITY_WARN,
                    "Contraseña restablecida",
                    "Nueva contraseña temporal: " + nueva);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al restablecer contraseña",
                    e.getMessage());
        }
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