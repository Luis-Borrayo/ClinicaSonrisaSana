package com.luisborrayo.clinicasonrisasana.repositories;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseRepository<T, ID> {

    @Inject
    protected EntityManager em;

    protected abstract Class<T> entity();

    public void tx(Consumer<EntityManager> work) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.accept(em);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public T save(T e) {
        tx(entityManager -> {
            Object id = entityManager.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(e);
            if (id == null) {
                entityManager.persist(e);   // nuevo
            } else {
                entityManager.merge(e);     // existente
            }
        });
        return e;
    }

    public T findId(ID id) {
        return em.find(entity(), id);
    }

    public void update(T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar entidad: " + e.getMessage(), e);
        }
    }

    public void delete(ID id) {
        try {
            em.getTransaction().begin();
            T entity = em.find(entity(), id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar entidad: " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        return em.createQuery("FROM " + entity().getSimpleName(), entity()).getResultList();
    }
}