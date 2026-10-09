package app.service;

import app.config.HibernateTestConfig;
import app.dao.AddressDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.event.EventDTORequest;
import app.dto.event.EventDTOResponse;
import app.entities.Event;
import app.entities.users.User;
import app.enums.EventCategory;
import app.exceptions.DatabaseIdException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceTest {
    private EntityManagerFactory emf;
    private EventService eventService;
    private EventDAO eventDAO;
    private AddressDAO addressDAO;
    private UserDAO userDAO;
    private AddressService addressService;
    private UserService userService;
    private Map<String, User> users;
    private Map<String, Event> events;
    private int userId;

    @BeforeAll
    void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        eventDAO = new EventDAO(emf);
        addressDAO = new AddressDAO(emf);
        userDAO = new UserDAO(emf);
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        users = TestDataCreator.createUsers(emf);
        events = TestDataCreator.createEvents(emf);
        addressService = new AddressService(addressDAO);
        userService = new UserService(userDAO);
        eventService = new EventService(eventDAO, addressService, userService);
        userId = users.get("admin").getId();
    }

    @Test
    public void create() {
        EventDTORequest eventDTORequest = new EventDTORequest(
                userId,
                "Copenhagen Jazz Night",
                "A fantastic evening with live jazz music in the heart of Copenhagen.",
                150.0,
                false,
                "København V",
                "1553",
                "H.C. Andersens Boulevard 44",
                LocalTime.of(19, 30),
                LocalDate.of(2026, 10, 15),
                "https://example.com/jazz-night",
                "https://example.com/images/jazz.jpg",
                false,
                EventCategory.MUSIC);

        EventDTOResponse eventDTOResponse = eventService.create(eventDTORequest);

        assertNotNull(eventDTOResponse);
        assertThat(eventDTOResponse.title(), is("Copenhagen Jazz Night"));
        assertThat(eventDTOResponse.price(), is(150.0));
        assertThat(eventDTOResponse.category().toLowerCase(), is(EventCategory.MUSIC.getLabel().toLowerCase()));
    }

    @Test
    public void getById() {
        EventDTORequest eventDTORequest = new EventDTORequest(
                userId,
                "Rock Concert",
                "Loud rock music",
                250.0,
                false,
                "København K",
                "1050",
                "Nyhavn 1",
                LocalTime.of(20, 0),
                LocalDate.of(2026, 11, 1),
                "https://example.com/rock",
                "https://example.com/images/rock.jpg",
                false,
                EventCategory.MUSIC);

        EventDTOResponse created = eventService.create(eventDTORequest);

        EventDTOResponse fetched = eventService.getById(created.id());

        assertNotNull(fetched);
        assertThat(fetched.id(), is(created.id()));
        assertThat(fetched.title(), is("Rock Concert"));
    }

    @Test
    public void getAll() {
        List<EventDTOResponse> allEvents = eventService.getAll();

        assertNotNull(allEvents);
        assertThat(allEvents.size(), is(not(0)));
    }

    @Test
    public void updateById() {
        EventDTORequest initialRequest = new EventDTORequest(
                userId,
                "Old Title",
                "Old Description",
                100.0,
                true,
                "København S",
                "2300",
                "Amager Strandvej 1",
                LocalTime.of(12, 0),
                LocalDate.of(2026, 12, 1),
                "https://example.com/old",
                "https://example.com/images/old.jpg",
                true,
                EventCategory.ATHLETIC_RACES);

        EventDTOResponse created = eventService.create(initialRequest);

        EventDTORequest updatedRequest = new EventDTORequest(
                userId,
                "Updated Jazz Festival",
                "New updated description",
                200.0,
                false,
                "København V",
                "1553",
                "Vesterbrogade 10",
                LocalTime.of(18, 0),
                LocalDate.of(2026, 12, 2),
                "https://example.com/updated",
                "https://example.com/images/updated.jpg",
                false,
                EventCategory.MUSIC);

        EventDTOResponse updated = eventService.updateById(created.id(), updatedRequest);

        assertNotNull(updated);
        assertThat(updated.title(), is("Updated Jazz Festival"));
        assertThat(updated.description(), is("New updated description"));
        assertThat(updated.price(), is(200.0));
        assertThat(updated.category().toLowerCase(), is(EventCategory.MUSIC.getLabel().toLowerCase()));
    }

    @Test
    public void deleteById() {
        EventDTORequest eventDTORequest = new EventDTORequest(
                userId,
                "Temporary Event",
                "To be deleted",
                50.0,
                false,
                "Frederiksberg",
                "2000",
                "Gammel Kongevej 1",
                LocalTime.of(14, 0),
                LocalDate.of(2026, 9, 1),
                "https://example.com/temp",
                "https://example.com/images/temp.jpg",
                false,
                EventCategory.CULTURAL);

        EventDTOResponse created = eventService.create(eventDTORequest);
        int eventId = created.id();

        boolean isDeleted = eventService.deleteById(eventId);
        assertThat(isDeleted, is(true));

        assertThrows(DatabaseIdException.class, () -> {eventService.getById(eventId);});
    }
}