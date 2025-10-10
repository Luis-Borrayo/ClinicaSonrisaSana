package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Citas;
import com.luisborrayo.clinicasonrisasana.repositories.CitasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CitasService {

    @Inject
    private CitasRepository citasRepository;

    public Citas crearCita(Citas cita) {
        // Validar que no exista cita en el mismo horario
        if (citasRepository.existeCitaEnHorario(cita.getOdontologo(), cita.getFechaCita())) {
            throw new RuntimeException("El odontólogo ya tiene una cita programada en ese horario");
        }

        citasRepository.crear(cita);
        return cita;
    }

    // ✅ AGREGAR ESTE MÉTODO FALTANTE
    public Citas actualizarCita(Citas cita) {
        // Para JDBC simple, podemos eliminar y crear de nuevo
        // O implementar un método update en el repository
        citasRepository.eliminar(cita.getId());
        citasRepository.crear(cita);
        return cita;
    }

    public List<Citas> obtenerTodasLasCitas() {
        return citasRepository.buscarTodas();
    }

    public void eliminarCita(Long id) {
        citasRepository.eliminar(id);
    }
}