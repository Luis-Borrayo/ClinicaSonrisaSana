package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class OdontologoRepository extends BaseRepository<Odontologo, Long> {

    @Override
    protected Class<Odontologo> entity(){
        return Odontologo.class;
    }

    public Odontologo findByColegiado(String colegiado) {
        TypedQuery<Odontologo> query = em.createQuery(
                "SELECT o FROM Odontologo o WHERE o.colegiado = :colegiado", Odontologo.class
        );
        query.setParameter("colegiado", colegiado);
        List<Odontologo> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Odontologo> findByEspecialidad(String especialidad) {
        TypedQuery<Odontologo> query = em.createQuery(
                "SELECT o FROM Odontologo o WHERE o.especialidad = :especialidad", Odontologo.class
        );
        query.setParameter("especialidad", especialidad);
        return query.getResultList();
    }
}
