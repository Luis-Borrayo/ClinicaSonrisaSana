package com.luisborrayo.clinicasonrisasana.services;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.repositories.OdontologoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class OdontologoService {

    private static final Logger LOGGER = Logger.getLogger(OdontologoService.class.getName());

    @Inject
    private OdontologoRepository odontologoRepository;

    // ==== CRUD BÁSICO PARA LAS NUEVAS PANTALLAS ====
    public List<Odontologo> listar() {
        return odontologoRepository.findAll();
    }

    public void guardar(Odontologo o) {
        odontologoRepository.save(o);
    }

    public void eliminar(Long id) {
        odontologoRepository.delete(id);
    }


    // ==== MÉTODOS EXISTENTES (NO TOCAR) ====
    public List<Odontologo> obtenerTodosLosOdontologos() {
        try {
            List<Odontologo> resultado = odontologoRepository.findAll();
            LOGGER.info("Odontólogos obtenidos: " + resultado.size());
            return resultado;
        } catch (Exception e) {
            LOGGER.severe("Error al obtener odontólogos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Odontologo> obtenerOdontologosActivos() {
        try {
            List<Odontologo> resultado = odontologoRepository.findByActivoTrue();
            LOGGER.info("Odontólogos activos obtenidos: " + resultado.size());
            return resultado;
        } catch (Exception e) {
            LOGGER.severe("Error al obtener odontólogos activos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Odontologo obtenerOdontologoPorId(Long id) {
        try {
            return odontologoRepository.findId(id);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener odontólogo por ID: " + id + " - " + e.getMessage());
            return null;
        }
    }


    public Odontologo obtenerPorColegiado(String colegiado) {
        return odontologoRepository.findByColegiado(colegiado);
    }

    public List<Odontologo> obtenerPorEspecialidad(Odontologo.Especialidad especialidad) {
        return odontologoRepository.findByEspecialidad(especialidad);
    }

    public Odontologo guardarOdontologo(Odontologo odontologo) {
        return odontologoRepository.save(odontologo);
    }

    public void eliminarOdontologo(Long id) {
        odontologoRepository.delete(id);
    }
}
