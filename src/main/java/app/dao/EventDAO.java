package app.dao;

import app.entities.Event;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class EventDAO extends GenericDAO<Event> {

    public EventDAO(EntityManagerFactory emf) {
        super(emf, Event.class, Event.class.getSimpleName());
    }

    public List<Event> readAllTmEvents() {
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                String jpql = "SELECT e FROM Event e WHERE e.sourceProvider = :provider";
                TypedQuery<Event> query = em.createQuery(jpql, Event.class);
                query.setParameter("provider", SourceProvider.API_TICKETMASTER);
                List<Event> events = query.getResultList();
                em.getTransaction().commit();
                return events;
            } catch (Exception e) {
                throw new DatabaseException("Reading all TicketMaster events failed", e);
            }
    }

    public List<Event> searchAndFilterEvent(String search, EventCategory category, LocalDate startDate, String postalCode, int page, int pageSize) {
        try (EntityManager em = emf.createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Event e WHERE 1=1");

            if (category != null) jpql.append(" AND e.category = :category");
            if (startDate != null) jpql.append(" AND e.startDate >= :startDate");
            if (postalCode != null && !postalCode.isBlank()) jpql.append(" AND e.location.postalCode = :postalCode");
            if (search != null && !search.isEmpty()) jpql.append(" AND (e.title LIKE :search OR e.description LIKE :search OR e.location.address LIKE :search OR e.location.postalCode LIKE :search OR e.category LIKE :search)");

            TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class);

            if (category != null) query.setParameter("category", category);
            if (startDate != null) query.setParameter("startDate", startDate);
            if (postalCode != null && !postalCode.isBlank()) query.setParameter("postalCode", postalCode);
            if (search != null && !search.isEmpty()) query.setParameter("search", "%" + search + "%");

            int offset = page * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            return query.getResultList();
        }catch (Exception e) {
            throw new DatabaseException("Search & filter failed", e);
        }
    }

    public List<Event> readEventsByUserId(int userId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT e FROM Event e JOIN User u WHERE u.userId = :userId";
            TypedQuery<Event> query = em.createQuery(jpql, Event.class);
            query.setParameter("userId", userId);
            List<Event> events = query.getResultList();
            em.getTransaction().commit();
            return events;
        }
    }

    public List<Event> readEventsByAdvertId(int advertId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT e FROM Event e JOIN Advert a WHERE a.advertId = :advertId";
            TypedQuery<Event> query = em.createQuery(jpql, Event.class);
            query.setParameter("advertId", advertId);
            List<Event> events = query.getResultList();
            em.getTransaction().commit();
            return events;
        }
    }

    public List<Event> readFavoritEventsByUserId(int userId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT a.favoriteEvents FROM Attendee a WHERE a.userId = :userId";
            TypedQuery<Event> query = em.createQuery(jpql, Event.class);
            query.setParameter("userId", userId);
            List<Event> events = query.getResultList();
            em.getTransaction().commit();
            return events;
        }
    }

    public List<Event> readLikedEventsByUserId(int userId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT a.likedEvents FROM Attendee a WHERE a.userId = :userId";
            TypedQuery<Event> query = em.createQuery(jpql, Event.class);
            query.setParameter("userId", userId);
            List<Event> events = query.getResultList();
            em.getTransaction().commit();
            return events;
        }
    }

    public List<EventCategory> readDistinctCategoriesInDb() {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT DISTINCT e.category FROM Event e WHERE e.category IS NOT NULL";
            TypedQuery<EventCategory> query = em.createQuery(jpql, EventCategory.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to read active categories", e);
        }
    }
}
