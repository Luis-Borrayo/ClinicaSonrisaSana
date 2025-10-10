package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Paciente;
import com.luisborrayo.clinicasonrisasana.repositories.PacienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PacienteService {

    @Inject
    private PacienteRepository pacienteRepository;

    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteRepository.buscarTodos();
    }

    public Paciente obtenerPacientePorId(Long id) {
        return pacienteRepository.buscarPorId(id);
    }
}