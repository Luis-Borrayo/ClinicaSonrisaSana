package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.repositories.OdontologoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class CatalogoOdontologosBean {

    @Inject
    private OdontologoRepository odontologoRepository;

    private List<Odontologo> odontologos;

    @PostConstruct
    public void init() {
        odontologos = odontologoRepository.findAll();
    }

    public List<Odontologo> getOdontologos() {
        return odontologos;
    }
}
