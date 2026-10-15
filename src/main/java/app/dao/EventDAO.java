package app.dao;

import app.entities.Event;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class EventDAO extends GenericDAO<Event> {

    public EventDAO(EntityManagerFactory emf) {
        super(emf, Event.class, Event.class.getSimpleName());
    }

    public List<Event> readAllTmEvents() {
            try (EntityManager em = emf.createEntityManager()) {
                String jpql = "SELECT e FROM Event e WHERE e.sourceProvider = :provider";
                return em.createQuery(jpql, Event.class)
                    .setParameter("provider", SourceProvider.API_TICKETMASTER)
                    .getResultList();
            } catch (PersistenceException e) {
                throw new DatabaseException("Reading all TicketMaster events failed", e);
            }
    }

    public List<Event> searchAndFilterEvent(String search, EventCategory category, LocalDate startDate, String postalCode, int page, int pageSize) {
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasPostalCode = postalCode != null && !postalCode.isBlank();

        try (EntityManager em = emf.createEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Event e LEFT JOIN e.location l WHERE 1=1");

            if (category != null) jpql.append(" AND e.category = :category");
            if (startDate != null) jpql.append(" AND e.startDate >= :startDate");
            if (hasPostalCode) jpql.append(" AND l.postalCode = :postalCode");
            if (hasSearch) jpql.append(" AND (LOWER(e.title) LIKE :search"
                    + " OR LOWER(e.description) LIKE :search"
                    + " OR LOWER(l.address) LIKE :search"
                    + " OR LOWER(l.city) LIKE :search"
                    + " OR l.postalCode LIKE :search)");
            jpql.append(" ORDER BY e.startDate ASC, e.eventId ASC");

            TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class);

            if (category != null) query.setParameter("category", category);
            if (startDate != null) query.setParameter("startDate", startDate);
            if (hasPostalCode) query.setParameter("postalCode", postalCode.trim());
            if (hasSearch) query.setParameter("search", "%" + search.trim().toLowerCase() + "%");

            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Search & filter failed", e);
        }
    }

    public List<Event> readEventsByUserId(int userId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT e FROM Event e LEFT JOIN e.organizer o LEFT JOIN e.admin a WHERE o.userId = :userId OR a.userId = :userId ORDER BY e.startDate ASC", Event.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading events for user " + userId + " failed", e);
        }
    }

    public List<EventCategory> readDistinctCategoriesInDb() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT DISTINCT e.category FROM Event e WHERE e.category IS NOT NULL",
                            EventCategory.class)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Failed to read active categories", e);
        }
    }
}
