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
        if (citasRepository.existeCitaEnHorario(cita.getOdontologo(), cita.getFechaCita())) {
            throw new RuntimeException("El odontólogo ya tiene una cita programada en ese horario");
        }

        citasRepository.crear(cita);
        return cita;
    }

    public Citas actualizarCita(Citas cita) {
        citasRepository.delete(cita.getId());
        citasRepository.crear(cita);
        return cita;
    }

    public List<Citas> obtenerTodasLasCitas() {
        return citasRepository.buscarTodas();
    }

    public void eliminarCita(Long id) {
        citasRepository.delete(id);
    }
}