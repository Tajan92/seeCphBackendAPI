package app.dao;

import app.entities.Advert;
import jakarta.persistence.EntityManagerFactory;

public class AdvertDAO extends GenericDAO<Advert> {

    public AdvertDAO(EntityManagerFactory emf) {
        super(emf, Advert.class, Advert.class.getSimpleName());
    }
}
