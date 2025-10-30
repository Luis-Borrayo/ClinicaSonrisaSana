package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.User;
import com.luisborrayo.clinicasonrisasana.repositories.BaseRepository;
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
    }

    public List<User> findActivos() {
        return em.createQuery("SELECT u FROM User u WHERE u.active = true", User.class).getResultList();
    }

    public List<User> findByRole(User.Role rol) {
        return em.createQuery("SELECT u FROM User u WHERE u.role = :rol", User.class)
                .setParameter("rol", rol)
                .getResultList();
    }

    public User findByCorreo(String correo) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.correo = :correo", User.class)
                .setParameter("correo", correo)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public User findByUsuario(String usuario) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.usuario = :usuario", User.class)
                .setParameter("usuario", usuario)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }



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