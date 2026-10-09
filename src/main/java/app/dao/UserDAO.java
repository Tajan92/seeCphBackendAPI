package app.dao;

import app.dto.user.UserDTOResponse;
import app.entities.users.User;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

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
            em.getTransaction().begin();
            User user = em.find(User.class, email);
            if (user == null) {
                throw new DatabaseException("User not with email: "+ email);
            }
            em.getTransaction().commit();
            return user;
        }
    }
}
