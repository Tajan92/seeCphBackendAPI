package app.daos;

import app.entities.events.Event;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class EventDAO implements IDAO<Event, Integer> {
    EntityManagerFactory emf;

    @Override
    public Event create(Event event) {
        return null;
    }

    @Override
    public Event read(Integer id) {
        return null;
    }

    @Override
    public Set<Event> readAll() {
        return Set.of();
    }

    @Override
    public Event update(Event event) {
        return null;
    }

    @Override
    public boolean delete(Event event) {
        return true;
    }
}
