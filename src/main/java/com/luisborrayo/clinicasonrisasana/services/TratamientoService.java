package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import com.luisborrayo.clinicasonrisasana.repositories.TratamientoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TratamientoService {

    @Inject
    private TratamientoRepository tratamientoRepository;

    public List<Tratamiento> obtenerTodosLosTratamientos() {
        try {
            List<Tratamiento> tratamientos = tratamientoRepository.findAll();
            if (tratamientos == null) {
                tratamientos = new ArrayList<>();
            }
            return tratamientos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Tratamiento obtenerTratamientoPorId(Long id) {
        try {
            return tratamientoRepository.findId(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void crearTratamiento(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    public void actualizarTratamiento(Tratamiento tratamiento) {
        tratamientoRepository.update(tratamiento);
    }

    public void eliminarTratamiento(Long id) {
        tratamientoRepository.delete(id);
    }
}