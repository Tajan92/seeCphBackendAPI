package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.entities.Event;
import app.enums.EventCategory;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private EventDAO eventDAO;
    private Map<String, Event> events;

    @BeforeEach
    void setUp() {
        events = TestDataCreator.createEvents(emf);
    }

    @BeforeAll
    void setUpAll() {
        eventDAO = new EventDAO(emf);
    }

    @Test
    void create() {
        Event event = Event.builder()
                .title("F.C. København vs. Brøndby IF")
                .description("Experience the intense New Firm derby live at Parken")
                .category(EventCategory.FOOTBALL)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .location(Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build())
                .price(350.00)
                .build();

        Event eventCreated = eventDAO.create(event);
        assertThat(eventCreated.getEventId(), notNullValue());

        Event eventFetched = eventDAO.read(eventCreated.getEventId());
        assertThat(eventFetched.getTitle(), is(event.getTitle()));
        assertThat(eventFetched.getEventId(), is(3));
        assertThat(eventFetched.getEventId(), is(eventCreated.getEventId()));
        assertThat(eventFetched.getCategory(), is(EventCategory.FOOTBALL));
        assertThat(eventFetched.getStartDate(), is(event.getStartDate()));
        assertThat(eventFetched.getLastSyncedAt().toLocalDate(), is(LocalDate.now()));
        assertThat(eventFetched.getLocation(), is(event.getLocation()));
    }

    @Test
    void read() {
        Event event = events.get("event");
        Event eventFetched = eventDAO.read(event.getEventId());
        assertThat(eventFetched.getTitle(), is(event.getTitle()));
        assertThat(eventFetched.getEventId(), is(event.getEventId()));
    }

    @Test
    void readAll() {
        Set<Event> allEvents = eventDAO.readAll();
        assertThat(allEvents, hasSize(2));
        assertThat(allEvents, containsInAnyOrder(events.values().toArray()));
    }

    @Test
    void update() {
        Event event = events.get("event");
        event.setPrice(350.00);

        Event fetchedEvent = eventDAO.update(event);

        assertThat(event.getLastSyncedAt(), not(fetchedEvent.getLastSyncedAt()));
        assertThat(fetchedEvent.getEventId(), is(event.getEventId()));
        assertThat(fetchedEvent.getPrice(), is(350.00));
        assertThat(fetchedEvent.getEventId(), is(event.getEventId()));
    }

    @Test
    void delete() {
        Event event = events.get("event");

        boolean deletedEvent = eventDAO.delete(event);
        assertThat(deletedEvent, is(true));
        assertThrows(DatabaseException.class, () -> eventDAO.read(event.getEventId()));
    }

    @Test
    void create_withNullEvent_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.create(null));
        assertThat(ex.getMessage(), is("Event is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.read(999_999));
        assertThat(ex.getMessage(), is("Event not found with id: 999999"));
    }

    @Test
    void update_withNullEvent_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.update(null));
        assertThat(ex.getMessage(), is("Event is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        Event missing = Event.builder()
                .eventId(999_999)
                .title("Missing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.update(missing));
        assertThat(ex.getMessage(), is("Updating Event failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.delete(null));
        assertThat(ex.getMessage(), is("Event is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Event missing = Event.builder()
                .eventId(999_999)
                .title("Missing")
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> eventDAO.delete(missing));
        assertThat(ex.getMessage(), is("Delete Event failed"));
    }
}