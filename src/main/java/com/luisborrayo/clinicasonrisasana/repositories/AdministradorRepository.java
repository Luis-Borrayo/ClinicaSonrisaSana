package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Administrador;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AdministradorRepository extends BaseRepository<Administrador, Long> {

    @Override
    protected Class<Administrador> entity() {
        return Administrador.class;
    }
}