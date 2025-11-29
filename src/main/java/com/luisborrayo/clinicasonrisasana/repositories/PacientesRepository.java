package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PacientesRepository extends BaseRepository<Pacientes, Long> {

    @Inject
    private EntityManager em;

    public PacientesRepository() {
    }

    @Override
    protected Class<Pacientes> entity() {
        return Pacientes.class;
    }

    public Optional<Pacientes> buscarPorEmail(String email) {
        try {
            Pacientes paciente = em.createQuery(
                            "SELECT p FROM Pacientes p WHERE p.correo = :email",
                            Pacientes.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(paciente);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Buscar paciente por DPI
     */
    public Optional<Pacientes> buscarPorDpi(String dpi) {
        try {
            Pacientes paciente = em.createQuery(
                            "SELECT p FROM Pacientes p WHERE p.dpi = :dpi",
                            Pacientes.class)
                    .setParameter("dpi", dpi)
                    .getSingleResult();
            return Optional.of(paciente);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Buscar pacientes por nombre (búsqueda parcial)
     */
    public List<Pacientes> buscarPorNombre(String nombre) {
        return em.createQuery(
                        "SELECT p FROM Pacientes p WHERE " +
                                "LOWER(p.nombre) LIKE LOWER(:nombre) OR " +
                                "LOWER(p.apellido) LIKE LOWER(:nombre) " +
                                "ORDER BY p.nombre, p.apellido",
                        Pacientes.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    /**
     * Obtener todos los pacientes ordenados por nombre
     */
    public List<Pacientes> obtenerTodosOrdenados() {
        return em.createQuery(
                        "SELECT p FROM Pacientes p ORDER BY p.nombre, p.apellido",
                        Pacientes.class)
                .getResultList();
    }

    /**
     * Verificar si existe un DPI
     */
    public boolean existeDpi(String dpi) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM Pacientes p WHERE p.dpi = :dpi",
                        Long.class)
                .setParameter("dpi", dpi)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Verificar si existe un DPI (excluyendo un ID específico para edición)
     */
    public boolean existeDpiExcluyendo(String dpi, Long idExcluir) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM Pacientes p WHERE p.dpi = :dpi AND p.id != :id",
                        Long.class)
                .setParameter("dpi", dpi)
                .setParameter("id", idExcluir)
                .getSingleResult();
        return count > 0;
    }
}