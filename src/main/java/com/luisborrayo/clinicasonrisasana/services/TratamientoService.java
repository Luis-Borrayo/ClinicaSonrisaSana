package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import com.luisborrayo.clinicasonrisasana.repositories.TratamientoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class TratamientoService {

    @Inject
    private TratamientoRepository tratamientoRepository;

    public List<Tratamiento> obtenerTodosLosTratamientos() {
        return tratamientoRepository.buscarTodos();
    }

    public Tratamiento obtenerTratamientoPorId(Long id) {
        return tratamientoRepository.buscarPorId(id);
    }
}