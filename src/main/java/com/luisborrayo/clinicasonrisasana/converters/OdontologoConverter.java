package com.luisborrayo.clinicasonrisasana.converters;

import com.luisborrayo.clinicasonrisasana.model.Odontologo;
import com.luisborrayo.clinicasonrisasana.services.OdontologoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "odontologoConverter", managed = true)
@ApplicationScoped
public class OdontologoConverter implements Converter<Odontologo> {

    @Inject
    OdontologoService service;

    @Override
    public Odontologo getAsObject(FacesContext ctx, UIComponent comp, String value) {
        // Si no hay selección, devuelve null para que 'required' funcione
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return service.obtenerOdontologoPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Odontologo value) {
        return (value == null || value.getId() == null) ? "" : value.getId().toString();
    }
}