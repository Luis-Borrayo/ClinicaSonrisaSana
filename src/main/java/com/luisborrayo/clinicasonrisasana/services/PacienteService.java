package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.repositories.PacientesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PacienteService {

    @Inject
    private PacientesRepository pacienteRepository;

    public List<Pacientes> obtenerTodosLosPacientes() {
        return pacienteRepository.findAll();
    }

    public Pacientes obtenerPacientePorId(Long id) {
        return pacienteRepository.findId(id);
    }
}