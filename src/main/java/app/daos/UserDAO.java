package app.daos;

import app.entities.users.User;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@AllArgsConstructor
public class UserDAO implements IDAO<User, Integer> {
    EntityManagerFactory emf;

    @Override
    public User create(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    @Override
    public User read(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = em.find(User.class, id);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    @Override
    public Set<User> readAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Set<User> users = new HashSet<>(em.createQuery("select u from User u", User.class).getResultList());
        em.getTransaction().commit();
        em.close();
        return users;
    }

    @Override
    public User update(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    @Override
    public boolean delete(User user) {
        if (user.getUserId() == null) {
            throw new ApiException(400, "User id is required");
        }
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            try {
                User user1 = read(user.getUserId());
                if (user1 != null) {
                    em.remove(user);
                } else {
                    throw new ApiException(404, "User not found");
                }
                em.getTransaction().commit();
            }  catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete user failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return true;
    }
}
