package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class TratamientoRepository extends BaseRepository<Tratamiento, Long> {

    @Override
    protected Class<Tratamiento> entity() {
        return Tratamiento.class;
    }

    @Inject
    private EntityManager em;

    public TratamientoRepository() {
    }

    public List<Tratamiento> buscarPorRangoCosto(Double costoMin, Double costoMax) {
        return em.createQuery(
                        "SELECT t FROM Tratamiento t WHERE t.costo BETWEEN :min AND :max ORDER BY t.costo",
                        Tratamiento.class)
                .setParameter("min", costoMin)
                .setParameter("max", costoMax)
                .getResultList();
    }

    public List<Tratamiento> buscarPorNombre(String nombre) {
        return em.createQuery(
                        "SELECT t FROM Tratamiento t WHERE LOWER(t.nombre) LIKE LOWER(:nombre)",
                        Tratamiento.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }
}