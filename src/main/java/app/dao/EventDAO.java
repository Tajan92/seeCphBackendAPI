package app.dao;

import app.entities.Event;
import jakarta.persistence.EntityManagerFactory;

public class EventDAO extends GenericDAO<Event> {

    public EventDAO(EntityManagerFactory emf) {
        super(emf, Event.class, Event.class.getSimpleName());
    }
}
