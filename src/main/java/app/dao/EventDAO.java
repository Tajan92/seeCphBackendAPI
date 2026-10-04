package app.dao;

import app.entities.Event;
import app.enums.SourceProvider;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
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
}
