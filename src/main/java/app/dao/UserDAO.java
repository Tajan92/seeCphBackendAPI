package app.dao;

import app.entities.users.User;
import jakarta.persistence.EntityManagerFactory;


public class UserDAO extends JigDAO<User> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }
}
