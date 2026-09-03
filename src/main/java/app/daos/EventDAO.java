package app.daos;

import app.entities.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class EventDAO implements IDAO<Event, Integer> {
    EntityManagerFactory emf;

    @Override
    public Event create(Event event) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(event);
        em.getTransaction().commit();
        em.close();
        return event;
    }

    @Override
    public Event read(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Event event = em.find(Event.class, id);
        em.getTransaction().commit();
        em.close();
        return event;
    }

    @Override
    public Set<Event> readAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Set<Event> events = new HashSet<>(em.createQuery("select e from Event e", Event.class).getResultList());
        return events;
    }

    @Override
    public Event update(Event event) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(event);
        em.getTransaction().commit();
        em.close();
        return event;
    }

    @Override
    public boolean delete(Event event) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(event);
        em.getTransaction().commit();
        em.close();
        return true;
    }
}
