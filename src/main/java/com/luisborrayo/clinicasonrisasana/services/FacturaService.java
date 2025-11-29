package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import com.luisborrayo.clinicasonrisasana.repositories.FacturasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FacturaService {

    @Inject
    private FacturasRepository facturasRepository;

    public Facturas save(Facturas factura) {
        return facturasRepository.save(factura);
    }

    public Facturas findById(Long id) {
        return facturasRepository.findId(id);
    }

    public List<Facturas> findAll() {
        return facturasRepository.findAll();
    }

    public void delete(Long id) {
        facturasRepository.delete(id);
    }

    public List<Facturas> findByPacienteId(Long pacienteId) {
        return facturasRepository.findByPacienteId(pacienteId);
    }

    public List<Facturas> findByEstado(Facturas.EstadoPago estado) {
        return facturasRepository.findByEstadoPago(estado);
    }

    public List<Facturas> findByFecha(LocalDateTime desde, LocalDateTime hasta) {
        return facturasRepository.findByFechaEmisionBetween(desde, hasta);
    }
}
