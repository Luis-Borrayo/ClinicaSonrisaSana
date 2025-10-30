package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.Recepcionista;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecepcionistaRepository extends BaseRepository<Recepcionista, Long> {

    @Override
    protected Class<Recepcionista> entity() {
        return Recepcionista.class;
    }
}
