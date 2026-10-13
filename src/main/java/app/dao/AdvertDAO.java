package app.dao;

import app.entities.Advert;
import app.enums.AddPlacement;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class AdvertDAO extends GenericDAO<Advert> {

    public AdvertDAO(EntityManagerFactory emf) {
        super(emf, Advert.class, Advert.class.getSimpleName());
    }

    public List<Advert> readAdvertsByUserId(int userId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT a FROM Advert a LEFT JOIN a.organizer o LEFT JOIN a.admin ad WHERE o.userId = :userId OR ad.userId = :userId ORDER BY a.startDate DESC", Advert.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Finding adverts with " + userId + " failed", e);
        }
    }

    public List<Advert> readActiveAdverts(AddPlacement placement, LocalDate today) {
        boolean hasPlacement = placement != null;

        try (EntityManager em = emf.createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT a FROM Advert a WHERE a.startDate <= :today AND a.endDate >= :today");

            if (hasPlacement) jpql.append(" AND a.addPlacement = :placement");
            jpql.append(" ORDER BY a.startDate ASC");

            TypedQuery<Advert> query = em.createQuery(jpql.toString(), Advert.class);
            query.setParameter("today", today);
            if (hasPlacement) query.setParameter("placement", placement);

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Finding active adverts failed", e);
        }
    }
}
