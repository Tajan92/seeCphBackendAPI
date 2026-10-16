package app.dao;

import app.entities.Event;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.FavoriteOrLikedEvent;
import app.enums.Status;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.*;

import java.util.List;

public class UserDAO extends GenericDAO<User> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }

    public boolean checkIfEmailExist(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        }
    }

    public User readUserByEmail(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading user by email failed", e);
        }
    }

    public void addFavoriteOrLikedEvent(int userId, int eventId, FavoriteOrLikedEvent favoriteOrLiked) {
        try (EntityManager em = emf.createEntityManager()) {
            Attendee attendee = checkIfAttendeeExist(em, userId);
            Event event = checkIfEventExist(em, eventId);
            try {
                em.getTransaction().begin();
                switch (favoriteOrLiked) {
                    case FAVORITE -> attendee.addFavoriteEvent(event);
                    case LIKED -> attendee.addLikedEvent(event);
                }
                attendee.addLikedEvent(event);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Adding " + favoriteOrLiked.name().toLowerCase() + " event failed", e);
            }
        }
    }

    public List<Event> readFavoriteOrLikedEvents(int userId, FavoriteOrLikedEvent favoriteOrLiked) {
        try (EntityManager em = emf.createEntityManager()) {
            checkIfAttendeeExist(em, userId);
            try {
                String query = switch (favoriteOrLiked) {
                    case LIKED ->
                            "SELECT e FROM Attendee a JOIN a.likedEvents e LEFT JOIN FETCH e.location WHERE a.userId = :userId ORDER BY e.startDate";
                    case FAVORITE ->
                            "SELECT e FROM Attendee a JOIN a.favoriteEvents e LEFT JOIN FETCH e.location WHERE a.userId = :userId ORDER BY e.startDate";
                };
                return em.createQuery(query, Event.class)
                        .setParameter("userId", userId)
                        .getResultList();
            } catch (PersistenceException e) {
                throw new DatabaseException("Reading events from attendee failed", e);
            }
        }
    }

    public void removeFavoriteOrLikedEvent(int userId, int eventId, FavoriteOrLikedEvent favoriteOrLiked) {
        try (EntityManager em = emf.createEntityManager()) {
            Attendee attendee = checkIfAttendeeExist(em, userId);
            Event event = checkIfEventExist(em, eventId);
            try {
                em.getTransaction().begin();
                switch (favoriteOrLiked) {
                    case FAVORITE -> attendee.removeFavoriteEvent(event);
                    case LIKED -> attendee.removeLikedEvent(event);
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Removing " + favoriteOrLiked.name().toLowerCase() + " event failed", e);
            }
        }
    }

    public List<Organizer> adminReadOrganizersByStatus(Status status) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT o FROM Organizer o WHERE o.accountStatus = :status ORDER BY o.userId", Organizer.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading " + status.name() + " organizers failed", e);
        }
    }

    public Organizer adminChangeStatusOfOrganizer(int organizerId, Status status) {
        try (EntityManager em = emf.createEntityManager()) {
            Organizer organizer = em.find(Organizer.class, organizerId);
            if (organizer == null) {
                throw new DatabaseIdException("Organizer with id " + organizerId + " not found");
            }
            try {
                em.getTransaction().begin();
                organizer.setAccountStatus(status);
                em.getTransaction().commit();
                return organizer;
            } catch (PersistenceException e) {
                throw new DatabaseException("Changing status of organizer failed", e);
            }
        }
    }

    private Attendee checkIfAttendeeExist(EntityManager em, int userId) {
        Attendee attendee = em.find(Attendee.class, userId);
        if (attendee == null) throw new DatabaseIdException("Attendee with id " + userId + " does not exist");
        return attendee;
    }

    private Event checkIfEventExist(EntityManager em, int eventId) {
        Event event = em.find(Event.class, eventId);
        if (event == null) throw new DatabaseIdException("Event with id " + eventId + " does not exist");
        return event;
    }
}
