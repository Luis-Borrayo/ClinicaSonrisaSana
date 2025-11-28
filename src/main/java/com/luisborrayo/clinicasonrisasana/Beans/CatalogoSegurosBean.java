package com.luisborrayo.clinicasonrisasana.Beans;

import com.luisborrayo.clinicasonrisasana.model.Facturas;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;

@Named
@RequestScoped
public class CatalogoSegurosBean {

    public List<Facturas.Seguro> getSeguros() {
        return Arrays.asList(Facturas.Seguro.values());
    }
}
