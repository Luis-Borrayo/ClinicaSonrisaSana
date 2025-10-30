package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    public UserService() { }

    public List<User> listar() {
        return userRepository.findAll();
    }

    public List<User> listarActivos() {
        return userRepository.findActivos();
    }

    public User guardar(User u) {
        return userRepository.save(u);
    }

    public void eliminar(Integer id) {
        if (id != null) {
            userRepository.delete(id.longValue()); // Convertir Integer a Long para BaseRepository
        }
    }

    public void eliminar(User user) {
        if (user != null && user.getId() != null) {
            userRepository.delete(user.getId().longValue()); // Convertir Integer a Long
        }
    }

    public User buscarPorId(Integer id) {
        if (id == null) return null;
        return userRepository.findId(id.longValue()); // Convertir Integer a Long
    }

    public User buscarPorUsuario(String usuario) {
        return userRepository.findByUsuario(usuario);
    }

    public User buscarPorCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }

    public User autenticar(String usuario, String password) {
        User user = userRepository.findByUsuario(usuario);
        if (user != null && user.isActive() && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void desactivar(User user) {
        userRepository.desactivar(user);
    }

    public void cambiarContrasena(User user, String nuevaContrasena) {
        userRepository.cambiarContrasena(user, nuevaContrasena);
    }

    public void asignarRol(User user, User.Role rol) {
        userRepository.asignarRol(user, rol);
    }

    public List<User> listarPorRol(User.Role rol) {
        return userRepository.findByRole(rol);
    }
}