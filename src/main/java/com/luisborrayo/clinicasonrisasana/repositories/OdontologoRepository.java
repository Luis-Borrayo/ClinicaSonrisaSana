package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OdontologoRepository extends BaseRepository<Odontologo, Long> {

    @Override
    protected Class<Odontologo> entity() {
        return Odontologo.class;
    }

    // AGREGAR ESTE MÉTODO QUE FALTA
    public List<Odontologo> findAll() {
        try {
            TypedQuery<Odontologo> query = em.createQuery(
                    "SELECT o FROM Odontologo o ORDER BY o.nombre",
                    Odontologo.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error en OdontologoRepository.findAll(): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // AGREGAR ESTE MÉTODO TAMBIÉN (que se usa en PacientesBean)
    public List<Odontologo> findByActivoTrue() {
        try {
            TypedQuery<Odontologo> query = em.createQuery(
                    "SELECT o FROM Odontologo o WHERE o.activo = true ORDER BY o.nombre",
                    Odontologo.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error en OdontologoRepository.findByActivoTrue(): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Odontologo findByColegiado(String colegiado) {
        try {
            TypedQuery<Odontologo> query = em.createQuery(
                    "SELECT o FROM Odontologo o WHERE o.colegiado = :colegiado", Odontologo.class
            );
            query.setParameter("colegiado", colegiado);
            List<Odontologo> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            System.err.println("Error en OdontologoRepository.findByColegiado(): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Odontologo> findByEspecialidad(Odontologo.Especialidad especialidad) {
        try {
            TypedQuery<Odontologo> query = em.createQuery(
                    "SELECT o FROM Odontologo o WHERE o.especialidad = :especialidad", Odontologo.class
            );
            query.setParameter("especialidad", especialidad);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error en OdontologoRepository.findByEspecialidad(): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}