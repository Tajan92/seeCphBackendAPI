package app.dao;

import app.entities.IGetId;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public abstract class GenericDAO<T extends IGetId> implements IDAO<T, Integer> {
    protected EntityManagerFactory emf;
    private final Class<T> clazz;
    private String entityName;

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName + " is required");
        }
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(entity);
                em.getTransaction().commit();
                return entity;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Error creating " + entityName, e);
            }
        }
    }

    @Override
    public T readById(Integer id) {
        if (id == null) {
            throw new DatabaseIdException("ID is required");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(clazz, id);
            em.getTransaction().commit();
            if (entity != null) {
                return entity;
            }
            throw new DatabaseIdException(entityName + " not found with id: " + id);
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading " + entityName + " failed", e);
        }
    }

    @Override
    public List<T> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            String jpql = "select t from " + clazz.getSimpleName() + " t";
            List<T> entities = new ArrayList<>(em.createQuery(jpql, clazz).getResultList());
            em.getTransaction().commit();
            return entities;
        } catch (Exception e) {
            throw new DatabaseException("Reading all " + entityName + "s failed", e);
        }
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName + " is required for update");
        }
        if (entity.getId() == null) {
            throw new DatabaseIdException(entityName + " id is required for update");
        }

        try (EntityManager em = emf.createEntityManager()) {
            if (em.find(clazz, entity.getId()) == null) {
                throw new DatabaseIdException(entityName + " not found with id: " + entity.getId());
            }
            try {
                em.getTransaction().begin();
                T merged = em.merge(entity);
                em.getTransaction().commit();
                return merged;
            } catch (Exception e) {
                e.printStackTrace();
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Updating " + entityName + " failed", e);
            }
        }
    }

    @Override
    public boolean delete(T entity) {
        if (entity == null) {
            throw new DatabaseException(entityName + " is required for deleting");
        }
        if (entity.getId() == null) {
            throw new DatabaseIdException(entityName + " id is required for deleting");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T managed = em.find(clazz, entity.getId());
            if (managed == null) {
                throw new DatabaseIdException(entityName + " not found with id: " + entity.getId());
            }
            try {
                em.remove(managed);
                em.getTransaction().commit();
                return true;
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Delete " + entityName + " failed", e);
            }
        }
    }
}


