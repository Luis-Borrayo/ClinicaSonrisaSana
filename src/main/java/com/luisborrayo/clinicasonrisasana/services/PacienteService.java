package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.repositories.PacientesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PacienteService {

    private static final Logger LOGGER = Logger.getLogger(PacienteService.class.getName());

    @Inject
    private PacientesRepository pacienteRepository;

    public List<Pacientes> obtenerTodosLosPacientes() {
        return pacienteRepository.obtenerTodosOrdenados();
    }

    public Pacientes obtenerPacientePorId(Long id) {
        return pacienteRepository.findId(id);
    }

    public Pacientes save(Pacientes paciente) {
        LOGGER.log(Level.INFO, "Guardando paciente: {0} {1}",
                new Object[]{paciente.getNombre(), paciente.getApellido()});
        return pacienteRepository.save(paciente);
    }

    public void delete(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El ID del paciente no puede ser nulo");
            }

            Pacientes paciente = obtenerPacientePorId(id);
            if (paciente == null) {
                throw new IllegalArgumentException("El paciente no existe");
            }

            pacienteRepository.delete(id);
        } catch (Exception e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el paciente", e);
        }
    }

    public List<Pacientes> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodosLosPacientes();
        }
        return pacienteRepository.buscarPorNombre(nombre.trim());
    }

    public Optional<Pacientes> buscarPorDpi(String dpi) {
        if (dpi == null || dpi.trim().isEmpty()) {
            return Optional.empty();
        }
        return pacienteRepository.buscarPorDpi(dpi.trim());
    }

    public Optional<Pacientes> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return pacienteRepository.buscarPorEmail(email.trim());
    }

    public boolean existeDpi(String dpi) {
        if (dpi == null || dpi.trim().isEmpty()) {
            return false;
        }
        return pacienteRepository.existeDpi(dpi.trim());
    }

    public boolean existeDpiExcluyendo(String dpi, Long idExcluir) {
        if (dpi == null || dpi.trim().isEmpty() || idExcluir == null) {
            return false;
        }
        return pacienteRepository.existeDpiExcluyendo(dpi.trim(), idExcluir);
    }
}