package com.luisborrayo.clinicasonrisasana.repositories;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

public abstract class BaseRepository<T, ID> {

    @Inject
    protected EntityManager em;

    protected abstract Class<T> entity();

    public T save(T entity) {
        try {
            // Verificar si es nuevo o actualización
            Object id = em.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(entity);

            // Iniciar transacción manualmente
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            T result;
            if (id == null) {
                // Nuevo registro
                em.persist(entity);
                em.flush();
                result = entity;
            } else {
                // Actualizar registro existente
                result = em.merge(entity);
                em.flush();
            }

            // Commit de la transacción
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

            System.out.println("✅ Entidad guardada exitosamente: " + entity.getClass().getSimpleName());
            return result;

        } catch (Exception e) {
            // Rollback en caso de error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Error en save(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar la entidad: " + e.getMessage(), e);
        }
    }

    public T findId(ID id) {
        try {
            return em.find(entity(), id);
        } catch (Exception e) {
            System.err.println("❌ Error en findId(): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void update(T entity) {
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            em.merge(entity);
            em.flush();

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

            System.out.println("✅ Entidad actualizada exitosamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Error en update(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la entidad: " + e.getMessage(), e);
        }
    }

    public void delete(ID id) {
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            T entity = em.find(entity(), id);
            if (entity != null) {
                em.remove(entity);
                em.flush();
            }

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

            System.out.println("✅ Entidad eliminada exitosamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Error en delete(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la entidad: " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        try {
            return em.createQuery("FROM " + entity().getSimpleName(), entity()).getResultList();
        } catch (Exception e) {
            System.err.println("❌ Error en findAll(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener todas las entidades: " + e.getMessage(), e);
        }
    }
}