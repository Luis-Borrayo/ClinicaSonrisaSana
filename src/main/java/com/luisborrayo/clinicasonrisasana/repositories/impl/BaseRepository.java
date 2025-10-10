package com.luisborrayo.clinicasonrisasana.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public abstract class BaseRepository<T> {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClinicaSonrisaSana");
    protected EntityManager em;
    private Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
        em = emf.createEntityManager();
    }

    public void create(T entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    public T find(Object id) {
        return em.find(entityClass, id);
    }

    public void update(T entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    public void delete(Object id) {
        em.getTransaction().begin();
        T entity = em.find(entityClass, id);
        if (entity != null) {
            em.remove(entity);
        }
        em.getTransaction().commit();
    }
    public List<T> findAll() {
        return em.createQuery("FROM "+entityClass.getSimpleName(),entityClass).getResultList();
    }

    public void close(){
        if (em.isOpen()) em.close();
    }
}
