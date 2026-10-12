package app.dao;

import app.dto.user.UserDTOResponse;
import app.entities.users.User;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.*;

import java.util.List;

public class UserDAO extends GenericDAO<User> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }

    public boolean checkIfEmailExist(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        }
    }

    public User readUserByEmail(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading user by email failed", e);
        }
    }
}
