package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, Long> {

    @Override
    protected Class<User> entity() {return  User.class;}

    @Inject
    private EntityManager em;

    public UserRepository() {
        // Constructor vacÃ­o obligatorio para CDI
    }

    // Listar usuarios activos
    public List<User> findActivos() {
        return em.createQuery("SELECT u FROM User u WHERE u.active = true", User.class).getResultList();
    }

    // Listar usuarios por rol
    public List<User> findByRole(User.Role rol) {
        return em.createQuery("SELECT u FROM User u WHERE u.role = :rol", User.class)
                .setParameter("rol", rol)
                .getResultList();
    }

    // Buscar usuario por correo
    public User findByCorreo(String correo) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.correo = :correo", User.class)
                .setParameter("correo", correo)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    // Buscar usuario por nombre de usuario
    public User findByUsuario(String usuario) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.usuario = :usuario", User.class)
                .setParameter("usuario", usuario)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }



    // Cambiar contraseÃ±a de un usuario
    public void cambiarContrasena(User user, String nuevaContrasena) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            user.setPassword(nuevaContrasena);
            em.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    // Asignar rol a un usuario
    public void asignarRol(User user, User.Role rol) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            user.setRole(rol);
            em.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    // Desactivar usuario (sin borrarlo)
    public void desactivar(User user) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            user.setActive(false);
            em.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}