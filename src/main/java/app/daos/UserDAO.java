package app.daos;

import app.entities.users.User;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

public class UserDAO implements IDAO<User, Integer> {
    EntityManagerFactory emf;

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User read(Integer id) {
        return null;
    }

    @Override
    public Set<User> readAll() {
        return Set.of();
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public boolean delete(User user) {
        return true;
    }
}
