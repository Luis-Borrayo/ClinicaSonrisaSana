package com.luisborrayo.clinicasonrisasana.repositories.impl;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class PacientesRepository extends BaseRepository<Pacientes> {

    public PacientesRepository() {
        super(Pacientes.class);
    }

    public List<Pacientes> findByNombre(String nombre) {
        TypedQuery<Pacientes> query = em.createQuery(
                "SELECT p FROM Pacientes p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)", Pacientes.class
        );
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }
}
