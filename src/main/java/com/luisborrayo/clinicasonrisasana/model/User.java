package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    public enum Role {
        ADMINISTRADOR,
        ODONTOLOGO,
        RECEPCIONISTA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Email(message = "Debe ingresar un correo válido")
    @Column(unique = true, nullable = false)
    private String correo;

    @NotBlank
    @Size(max = 60)
    private String nombres;

    @NotBlank
    @Size(max = 60)
    private String apellidos;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String usuario;

    @NotBlank
    @Size(min = 6, message = "La contraseña debe contener al menos 6 caracteres")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    public User() {}

    public User(String correo, String nombres, String apellidos, String usuario, String password, Role role, boolean active) {
        this.correo = correo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Integer getId() {
        return id; }
    public void setId(Integer id) {
        this.id = id; }

    public String getCorreo() {
        return correo; }
    public void setCorreo(String correo) {
        this.correo = correo; }

    public String getNombres() {
        return nombres; }
    public void setNombres(String nombres) {
        this.nombres = nombres; }

    public String getApellidos() {
        return apellidos; }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos; }

    public String getUsuario() {
        return usuario; }
    public void setUsuario(String usuario) {
        this.usuario = usuario; }

    public String getPassword() {
        return password; }
    public void setPassword(String password) {
        this.password = password; }

    public Role getRole() {
        return role; }
    public void setRole(Role role) {
        this.role = role; }

    public boolean isActive() {
        return active; }
    public void setActive(boolean active) {
        this.active = active; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", correo='" + correo + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", usuario='" + usuario + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}