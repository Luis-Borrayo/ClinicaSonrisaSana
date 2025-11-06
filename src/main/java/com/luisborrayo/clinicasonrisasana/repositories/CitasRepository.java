package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Citas;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CitasRepository extends BaseRepository<Citas, Long> {

    @Override
    protected Class<Citas> entity() {
        return Citas.class;
    }

    @Inject
    private EntityManager em;

    public CitasRepository() {
    }

    // CREATE
    public void crear(Citas cita) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            em.persist(cita);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error creando cita: " + e.getMessage(), e);
        }
    }

    public List<Citas> buscarTodas() {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "ORDER BY c.fechaCita DESC",
                        Citas.class)
                .getResultList();
    }

    public void actualizar(Citas cita) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            em.merge(cita);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error actualizando cita: " + e.getMessage(), e);
        }
    }

    public boolean existeCitaEnHorario(Odontologo odontologo, LocalDateTime fechaCita) {
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Citas c WHERE c.odontologo.id = :odontologoId " +
                                "AND c.fechaCita = :fechaCita AND c.estado != :estadoCancelado",
                        Long.class)
                .setParameter("odontologoId", odontologo.getId())
                .setParameter("fechaCita", fechaCita)
                .setParameter("estadoCancelado", Citas.Estados.CANCELADA)
                .getSingleResult();

        return count > 0;
    }

    public List<Citas> buscarPorOdontologo(Long odontologoId) {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "WHERE c.odontologo.id = :odontologoId " +
                                "ORDER BY c.fechaCita DESC",
                        Citas.class)
                .setParameter("odontologoId", odontologoId)
                .getResultList();
    }

    public List<Citas> buscarPorPaciente(Long pacienteId) {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "WHERE c.paciente.id = :pacienteId " +
                                "ORDER BY c.fechaCita DESC",
                        Citas.class)
                .setParameter("pacienteId", pacienteId)
                .getResultList();
    }

    public List<Citas> buscarPorEstado(Citas.Estados estado) {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "WHERE c.estado = :estado " +
                                "ORDER BY c.fechaCita DESC",
                        Citas.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    public List<Citas> buscarCitasDelDia(Long odontologoId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "WHERE c.odontologo.id = :odontologoId " +
                                "AND c.fechaCita BETWEEN :fechaInicio AND :fechaFin " +
                                "ORDER BY c.fechaCita ASC",
                        Citas.class)
                .setParameter("odontologoId", odontologoId)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();
    }

    public List<Citas> buscarCitasPendientes() {
        return em.createQuery(
                        "SELECT c FROM Citas c " +
                                "LEFT JOIN FETCH c.paciente " +
                                "LEFT JOIN FETCH c.odontologo " +
                                "LEFT JOIN FETCH c.tratamiento " +
                                "WHERE c.estado = :estadoPendiente " +
                                "AND c.fechaCita >= :fechaActual " +
                                "ORDER BY c.fechaCita ASC",
                        Citas.class)
                .setParameter("estadoPendiente", Citas.Estados.PENDIENTE)
                .setParameter("fechaActual", LocalDateTime.now())
                .getResultList();
    }
}