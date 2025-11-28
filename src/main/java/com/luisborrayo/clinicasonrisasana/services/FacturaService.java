package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import com.luisborrayo.clinicasonrisasana.repositories.FacturasRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FacturaService {

    @Inject
    FacturasRepository facturasRepository;

    public Facturas save(Facturas f) {
        return facturasRepository.save(f);
    }

    public List<Facturas> findAll() {
        return facturasRepository.findAll();
    }

    public List<Facturas> findByPacienteId(Long pacienteId) {
        return facturasRepository.findByPacienteId(pacienteId);
    }
}
