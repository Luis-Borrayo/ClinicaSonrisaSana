package com.luisborrayo.clinicasonrisasana.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    protected abstract Class<T> entity();

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entity().getSimpleName() + " e", entity())
                .getResultList();
    }

    public T find(ID id) {
        return em.find(entity(), id);
    }

    public Optional<T> findOptional(ID id) {
        return Optional.ofNullable(em.find(entity(), id));
    }

    @Transactional
    public T save(T entity) {
        if (isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Transactional
    public void delete(T entity) {
        if (em.contains(entity)) {
            em.remove(entity);
        } else {
            em.remove(em.merge(entity));
        }
    }

    @Transactional
    public void deleteById(ID id) {
        T entity = find(id);
        if (entity != null) {
            delete(entity);
        }
    }

    private boolean isNew(T entity) {
        try {
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
            return id == null || (id instanceof Number && ((Number) id).longValue() == 0);
        } catch (Exception e) {
            return true;
        }
    }
}