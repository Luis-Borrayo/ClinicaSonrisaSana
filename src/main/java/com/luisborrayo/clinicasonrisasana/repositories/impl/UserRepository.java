package com.luisborrayo.clinicasonrisasana.repositories.impl;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.repositories.impl.BaseRepository;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UserRepository extends BaseRepository<User> {
    public UserRepository() {
        super(User.class);
    }
    public User findByCorreo(String correo) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.correo = :correo", User.class
        );
        query.setParameter("correo", correo);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public User findByUsuario(String usuario) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.usuario = :usuario", User.class
        );
        query.setParameter("usuario", usuario);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<User> findByRole(User.Role role) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class
        );
        query.setParameter("role", role);
        return query.getResultList();
    }

}