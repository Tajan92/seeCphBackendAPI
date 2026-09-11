package app.daos;

import app.entities.Advert;
import jakarta.persistence.EntityManagerFactory;

public class AdvertDAO extends JigDAO<Advert> {

    public AdvertDAO(EntityManagerFactory emf) {
        super(emf, Advert.class, Advert.class.getSimpleName());
    }
}
