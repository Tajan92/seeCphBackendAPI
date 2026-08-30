package app.daos;

import app.entities.Advert;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class AdvertDAO implements IDAO<Advert, Integer> {
    private EntityManagerFactory emf;

    @Override
    public Advert create(Advert advert) {
        return null;
    }

    @Override
    public Advert read(Integer id) {
        return null;
    }

    @Override
    public Set<Advert> readAll() {
        return Set.of();
    }

    @Override
    public Advert update(Advert advert) {
        return null;
    }

    @Override
    public boolean delete(Advert advert) {
        return true;
    }
}
