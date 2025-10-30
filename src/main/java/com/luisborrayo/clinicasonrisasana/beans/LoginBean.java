package com.luisborrayo.clinicasonrisasana.beans;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private String usuario;
    private String password;
    private User currentUser;
    private boolean rememberMe;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext().getSessionMap().get("currentUser") instanceof User) {
            this.currentUser = (User) context.getExternalContext().getSessionMap().get("currentUser");
        }
    }

    public void login() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        if (usuario == null || usuario.trim().isEmpty()) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Advertencia", "Debe ingresar un usuario"));
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Advertencia", "Debe ingresar una contraseña"));
            return;
        }

        try {
            User user = userService.autenticar(usuario.trim(), password);

            if (user != null) {
                this.currentUser = user;

                externalContext.getSessionMap().put("currentUser", user);
                externalContext.getSessionMap().put("usuario", user.getUsuario());
                externalContext.getSessionMap().put("role", user.getRole().toString());
                externalContext.getSessionMap().put("auth", true);

                this.password = null;

                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Bienvenido", user.getNombres() + " " + user.getApellidos()));

                String contextPath = externalContext.getRequestContextPath();
                String redirectPage = getRedirectPageByRole(user.getRole());
                externalContext.redirect(contextPath + redirectPage);

            } else {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error de autenticación",
                                "Usuario o contraseña incorrectos, o el usuario está inactivo"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "No se pudo redirigir a la página principal"));
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error del sistema",
                            "Ha ocurrido un error al iniciar sesión"));
        }
    }


    private String getRedirectPageByRole(User.Role role) {
        switch (role) {
            case ADMINISTRADOR:
                return "/principaladmin.xhtml";
            case ODONTOLOGO:
                return "/principalodontologo.xhtml";
            case RECEPCIONISTA:
                return "/principalrecepcionista.xhtml";
            default:
                return "/login.xhtml";
        }
    }

    public void logout() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.invalidateSession();

            this.currentUser = null;
            this.usuario = null;
            this.password = null;
            this.rememberMe = false;

            String contextPath = externalContext.getRequestContextPath();
            externalContext.redirect(contextPath + "/login.xhtml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(User.Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public boolean isAdmin() {
        return hasRole(User.Role.ADMINISTRADOR);
    }

    public boolean isOdontologo() {
        return hasRole(User.Role.ODONTOLOGO);
    }

    public boolean isRecepcionista() {
        return hasRole(User.Role.RECEPCIONISTA);
    }

    public String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getNombres() + " " + currentUser.getApellidos();
        }
        return "";
    }

    public String getCurrentUserRole() {
        if (currentUser != null && currentUser.getRole() != null) {
            return currentUser.getRole().toString();
        }
        return "";
    }

    public String getCurrentUserRoleLowerCase() {
        if (currentUser != null && currentUser.getRole() != null) {
            return currentUser.getRole().toString().toLowerCase();
        }
        return "";
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}