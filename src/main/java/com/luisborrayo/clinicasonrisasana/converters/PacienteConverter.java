package com.luisborrayo.clinicasonrisasana.converters;

import com.luisborrayo.clinicasonrisasana.model.Pacientes;
import com.luisborrayo.clinicasonrisasana.services.PacienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.convert.Converter;


@Named
@ApplicationScoped
public class PacienteConverter implements Converter<Pacientes> {

    @Inject
    private PacienteService pacienteService;

    @Override
    public Pacientes getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        return pacienteService.obtenerPacientePorId(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Pacientes value) {
        if (value == null) return "";
        return value.getId().toString();
    }
}