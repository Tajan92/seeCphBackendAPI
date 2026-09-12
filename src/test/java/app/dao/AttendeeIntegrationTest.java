package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Event;
import app.entities.users.Attendee;
import app.entities.users.User;
import app.enums.UserRole;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AttendeeIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private UserDAO userDAO;
    private Map<String, User> users;
    private Map<String, Event> events;

    @BeforeEach
    void setUp() {
        users = TestDataCreator.createUsers(emf);
        events = TestDataCreator.createEvents(emf);
    }

    @BeforeAll
    void setUpAll() {
        userDAO = new UserDAO(emf);
    }

    @Test
    void createAttendeeUpdateWithSets() {
        Attendee attendee = Attendee.builder()
                .name("Peter")
                .email("peter@mail.dk")
                .phone("67890123")
                .password("67890123")
                .userRole(UserRole.ATTENDEE)
                .build();

        Attendee createdAttendee = (Attendee) userDAO.create(attendee);

        createdAttendee.addFavoriteEvent(events.get("event"));
        createdAttendee.addLikedEvent(events.get("event"));

        Attendee updatedAttendee = (Attendee) userDAO.update(createdAttendee);

        assertThat(updatedAttendee.getFavoriteEvents().size(), is(createdAttendee.getFavoriteEvents().size()));
        assertThat(updatedAttendee.getLikedEvents().size(), is(createdAttendee.getLikedEvents().size()));
    }

    @Test
    void createAttendeeUpdateWithSetsNotFound() {
        Attendee attendee = Attendee.builder()
                .name("Peter")
                .email("peter@mail.dk")
                .phone("67890123")
                .password("67890123")
                .userRole(UserRole.ATTENDEE)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> attendee.addLikedEvent(events.get("NoEvent2")));
        assertThat(ex.getMessage(), is("Event not found"));

        DatabaseException ex2 = assertThrows(DatabaseException.class, () -> attendee.addFavoriteEvent(events.get("NoEvent")));
        assertThat(ex2.getMessage(), is("Event not found"));
    }

    @Test
    void createAttendeeWithSets() {
        Attendee attendee = Attendee.builder()
                .name("Peter")
                .email("peter@mail.dk")
                .phone("67890123")
                .password("67890123")
                .userRole(UserRole.ATTENDEE)
                .build();

        attendee.addFavoriteEvent(events.get("event"));
        attendee.addLikedEvent(events.get("event"));

        Attendee createdAttendee = (Attendee) userDAO.create(attendee);

        assertThat(createdAttendee, is(notNullValue()));
        assertThat(createdAttendee.getFavoriteEvents().size(), is(attendee.getFavoriteEvents().size()));
        assertThat(createdAttendee.getLikedEvents().size(), is(attendee.getLikedEvents().size()));
    }

    @Test
    void updateAttendeeAndRemovedEvent() {
        Attendee attendee = (Attendee) users.get("attendee");
        attendee.addFavoriteEvent(events.get("event"));
        attendee.addLikedEvent(events.get("event"));

        Attendee updatedAttendee = (Attendee) userDAO.update(attendee);

        assertThat(updatedAttendee.getFavoriteEvents().size(), is(attendee.getFavoriteEvents().size()));
        assertThat(updatedAttendee.getLikedEvents().size(), is(attendee.getLikedEvents().size()));

        updatedAttendee.getFavoriteEvents().remove(events.get("event"));
        updatedAttendee.getLikedEvents().remove(events.get("event"));

        Attendee updatedAttendee2 = (Attendee) userDAO.update(updatedAttendee);

        assertThat(updatedAttendee2.getFavoriteEvents().size(), is(updatedAttendee.getFavoriteEvents().size()));
        assertThat(updatedAttendee2.getLikedEvents().size(), is(updatedAttendee.getLikedEvents().size()));
    }
}
