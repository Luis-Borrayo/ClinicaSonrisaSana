package com.luisborrayo.clinicasonrisasana.repositories.impl;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class FacturasRepository extends BaseRepository<Facturas> {

    public FacturasRepository() {
        super(Facturas.class);
    }

    public List<Facturas> findByPacienteId(Long pacienteId) {
        TypedQuery<Facturas> query = em.createQuery(
                "SELECT f FROM Facturas f WHERE f.paciente.id = :id", Facturas.class
        );
        query.setParameter("id", pacienteId);
        return query.getResultList();
    }

    public List<Facturas> findByEstadoPago(Facturas.EstadoPago estado) {
        TypedQuery<Facturas> query = em.createQuery(
                "SELECT f FROM Facturas f WHERE f.estadoPago = :estado", Facturas.class
        );
        query.setParameter("estado", estado);
        return query.getResultList();
    }

    public List<Facturas> findByFechaEmisionBetween(LocalDateTime desde, LocalDateTime hasta) {
        TypedQuery<Facturas> query = em.createQuery(
                "SELECT f FROM Facturas f WHERE f.fechaEmision BETWEEN :desde AND :hasta", Facturas.class
        );
        query.setParameter("desde", desde);
        query.setParameter("hasta", hasta);
        return query.getResultList();
    }
}
