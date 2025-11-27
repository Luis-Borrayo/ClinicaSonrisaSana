package com.luisborrayo.clinicasonrisasana.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.convert.ConverterException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter<LocalDate> {

    private static final String PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException ex) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Fecha inválida",
                    "Formato esperado: " + PATTERN
            );
            throw new ConverterException(msg);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        if (value == null) {
            return "";
        }
        return value.format(FORMATTER);
    }
}
