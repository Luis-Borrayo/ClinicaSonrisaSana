package com.luisborrayo.clinicasonrisasana.repositories;

import com.luisborrayo.clinicasonrisasana.model.*;
import com.luisborrayo.clinicasonrisasana.repositories.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DataInitializer {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AdministradorRepository adminRepository;

    @Inject
    private OdontologoRepository odontologoRepository;

    @Inject
    private RecepcionistaRepository recepcionistaRepository;

    @PostConstruct
    public void init() {
        // Verificar si ya existen usuarios
        if (userRepository.findAll().isEmpty()) {
            crearUsuariosBase();
        }
    }

    private void crearUsuariosBase() {
        try {
            User userAdmin = new User(
                    "admin@clinica.com",
                    "Admin",
                    "Sistema",
                    "admin",
                    "123456", // Considera usar BCrypt después
                    User.Role.ADMINISTRADOR,
                    true
            );
            userAdmin = userRepository.save(userAdmin);

            Administrador admin = new Administrador(userAdmin);
            adminRepository.save(admin);

            User userOdontologo = new User(
                    "odontologo@clinica.com",
                    "Dr. Juan",
                    "Pérez",
                    "drjuan",
                    "123456",
                    User.Role.ODONTOLOGO,
                    true
            );
            userOdontologo = userRepository.save(userOdontologo);

            Odontologo odontologo = new Odontologo(
                    userOdontologo,
                    "C-1001",
                    "5 años",
                    Odontologo.Especialidad.ODONTOLOGO_GENERAL
            );
            odontologoRepository.save(odontologo);

            User userRecepcion = new User(
                    "recepcion@clinica.com",
                    "María",
                    "López",
                    "recepcion",
                    "123456",
                    User.Role.RECEPCIONISTA,
                    true
            );
            userRecepcion = userRepository.save(userRecepcion);

            Recepcionista recepcionista = new Recepcionista(userRecepcion);
            recepcionistaRepository.save(recepcionista);

            System.out.println("✅ Usuarios base creados exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al crear usuarios base: " + e.getMessage());
            e.printStackTrace();
        }
    }
}