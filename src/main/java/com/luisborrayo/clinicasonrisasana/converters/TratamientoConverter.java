package com.luisborrayo.clinicasonrisasana.converters;

import com.luisborrayo.clinicasonrisasana.model.Tratamiento;
import com.luisborrayo.clinicasonrisasana.services.TratamientoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.convert.Converter;
@Named
@ApplicationScoped
public class TratamientoConverter implements Converter<Tratamiento> {

    @Inject
    private TratamientoService tratamientoService;

    @Override
    public Tratamiento getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        return tratamientoService.obtenerTratamientoPorId(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Tratamiento value) {
        if (value == null) return "";
        return value.getId().toString();
    }
}