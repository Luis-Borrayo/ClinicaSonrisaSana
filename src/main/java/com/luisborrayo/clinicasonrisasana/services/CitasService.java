package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Citas;
import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.repositories.CitasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class CitasService {

    @Inject
    private CitasRepository citasRepository;

    @Inject
    private OdontologoService odontologoService;

    public Citas crearCita(Citas cita) {
        if (citasRepository.existeCitaEnHorario(cita.getOdontologo(), cita.getFechaCita())) {
            throw new RuntimeException("El odontólogo ya tiene una cita programada en ese horario");
        }

        citasRepository.crear(cita);
        return cita;
    }

    public Citas actualizarCita(Citas cita) {
        citasRepository.actualizar(cita);
        return cita;
    }

    public List<Citas> obtenerTodasLasCitas() {
        return citasRepository.buscarTodas();
    }

    public void eliminarCita(Long id) {
        citasRepository.delete(id);
    }

    public Citas findById(Long id) {
        return citasRepository.findId(id);
    }

    private boolean validarJornadaLaboral(Odontologo odontologo, LocalDateTime fechaCita) {
        // Aquí implementa la lógica según la jornada configurada del odontólogo
        // Ejemplo básico:
        DayOfWeek dia = fechaCita.getDayOfWeek();
        LocalTime hora = fechaCita.toLocalTime();

        // Lunes a Viernes de 9:00 a 17:00 (ajusta según tu lógica)
        return !dia.equals(DayOfWeek.SATURDAY) &&
                !dia.equals(DayOfWeek.SUNDAY) &&
                hora.isAfter(LocalTime.of(8, 59)) &&
                hora.isBefore(LocalTime.of(17, 1));
    }
}