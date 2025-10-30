package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.repositories.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class PacientesRepository extends BaseRepository<Pacientes, Long> {

    @Override
    protected Class<Pacientes> entity() {
        return Pacientes.class;
    }

    @Inject
    private EntityManager em;

    public PacientesRepository() {
    }


    public Pacientes buscarPorEmail(String email) {
        List<Pacientes> result = em.createQuery(
                        "SELECT p FROM Pacientes p WHERE p.correo = :email",
                        Pacientes.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Pacientes buscarPorTelefono(String telefono) {
        List<Pacientes> result = em.createQuery(
                        "SELECT p FROM Pacientes p WHERE p.contacto = :telefono",
                        Pacientes.class)
                .setParameter("telefono", telefono)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Pacientes> buscarPorNombre(String nombre) {
        return em.createQuery(
                        "SELECT p FROM Pacientes p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre",
                        Pacientes.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }
}