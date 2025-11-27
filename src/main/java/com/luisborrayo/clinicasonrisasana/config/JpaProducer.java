package com.luisborrayo.clinicasonrisasana.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.AvailableSettings;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class JpaProducer {

    private static final System.Logger LOGGER = System.getLogger(JpaProducer.class.getName());

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        LOGGER.log(System.Logger.Level.INFO, "🔧 Creando EntityManagerFactory...");

        Map<String, Object> props = new HashMap<>();

        // Configuración de base de datos
        props.put("jakarta.persistence.jdbc.driver", System.getProperty("DB_DRIVER"));
        props.put("jakarta.persistence.jdbc.url", System.getProperty("DB_URL"));
        props.put("jakarta.persistence.jdbc.user", System.getProperty("DB_USER"));
        props.put("jakarta.persistence.jdbc.password", System.getProperty("DB_PASSWORD"));

        // Configuración de Hibernate
        props.put("hibernate.dialect", System.getProperty("HIBERNATE_DIALECT"));
        props.put("hibernate.hbm2ddl.auto", System.getProperty("HIBERNATE_DDL"));
        props.put("hibernate.show_sql", System.getProperty("HIBERNATE_SHOW_SQL"));
        props.put("hibernate.format_sql", System.getProperty("HIBERNATE_FORMAT_SQL"));
        props.put("hibernate.archive.autodetection", "class");

        // ✅ RESOURCE_LOCAL para Tomcat (sin JTA)
        props.put("hibernate.transaction.coordinator_class", "jdbc");

        // Detectar entidades automáticamente
        Set<Class<?>> entities = new Reflections("com.luisborrayo.clinicasonrisasana.model")
                .getTypesAnnotatedWith(Entity.class);
        props.put(AvailableSettings.LOADED_CLASSES, new ArrayList<>(entities));

        LOGGER.log(System.Logger.Level.INFO, "✅ Entidades detectadas: " + entities.size());

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClinicaPU", props);
        LOGGER.log(System.Logger.Level.INFO, "✅ EntityManagerFactory creado exitosamente");

        return emf;
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager(EntityManagerFactory emf) {
        LOGGER.log(System.Logger.Level.DEBUG, "📦 Creando EntityManager para request");
        return emf.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            LOGGER.log(System.Logger.Level.DEBUG, "🔒 Cerrando EntityManager");
            em.close();
        }
    }
}