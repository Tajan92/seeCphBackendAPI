package app.daos;

import app.entities.Advert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class AdvertDAO implements IDAO<Advert, Integer> {
    private EntityManagerFactory emf;

    @Override
    public Advert create(Advert advert) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(advert);
        em.getTransaction().commit();
        em.close();
        return advert;
    }

    @Override
    public Advert read(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Advert advert = em.find(Advert.class, id);
        em.getTransaction().commit();
        em.close();
        return advert;
    }

    @Override
    public Set<Advert> readAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Set<Advert> adverts = new HashSet<>(em.createQuery("select a from Advert a", Advert.class).getResultList());
        em.getTransaction().commit();
        em.close();
        return adverts;
    }

    @Override
    public Advert update(Advert advert) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(advert);
        em.getTransaction().commit();
        em.close();
        return advert;
    }

    @Override
    public boolean delete(Advert advert) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(advert);
        em.getTransaction().commit();
        em.close();
        return true;
    }
}
