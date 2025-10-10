package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.repositories.OdontologoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OdontologoService {

    @Inject
    private OdontologoRepository odontologoRepository;

    public List<Odontologo> obtenerTodosLosOdontologos() {
        return odontologoRepository.buscarTodos();
    }

    public Odontologo obtenerOdontologoPorId(Long id) {
        return odontologoRepository.buscarPorId(id);
    }
}