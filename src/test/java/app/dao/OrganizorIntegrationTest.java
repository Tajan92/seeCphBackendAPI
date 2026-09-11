package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.enums.EventCategory;
import app.enums.Status;
import app.enums.UserRole;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrganizorIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private UserDAO userDAO;
    private AdvertDAO advertDAO;
    private EventDAO eventDAO;
    private Map<String, User> users;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        users = TestDataCreator.createUsers(emf);
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
        organizer = Organizer.builder()
                .organizerName("Run and Fun")
                .name("RF")
                .email("runfun@mail.dk")
                .password("123456")
                .userRole(UserRole.ORGANIZER)
                .accountStatus(Status.ACTIVE)
                .phone("89765432")
                .build();
    }

    @BeforeAll
    void setUpAll() {
        userDAO = new UserDAO(emf);
        eventDAO = new EventDAO(emf);
        advertDAO = new AdvertDAO(emf);
    }

    @AfterAll
    void tearDown() {
        emf.close();
    }

    @Test
    void createOrganizerUpdateWithEventAndAdvert() {
        Organizer fetchedOrganizer = (Organizer) userDAO.create(organizer);

        fetchedOrganizer.addAdvert(adverts.get("advert"));
        fetchedOrganizer.addAdvert(adverts.get("advert2"));

        fetchedOrganizer.addEvent(events.get("event"));
        fetchedOrganizer.addEvent(events.get("event2"));

        Organizer updatedOrganizer = (Organizer) userDAO.update(fetchedOrganizer);
        assertThat(updatedOrganizer.getAdverts().size(), is(2));
        assertThat(updatedOrganizer.getEvents().size(), is(2));
    }

    @Test
    void createOrganizerWithCascadePersist() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(150.00)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .build();

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .categories(Set.of(EventCategory.SPORT))
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(18, 30))
                .startDates(Set.of(LocalDate.of(2026, 10, 18)))
                .price(100.00)
                .location("Copenhagen")
                .build();

        organizer.addAdvert(advert);
        organizer.addEvent(event);

        Organizer savedOrganizer = (Organizer) userDAO.create(organizer);

        assertThat(savedOrganizer.getUserId(), notNullValue());
        assertThat(savedOrganizer.getAdverts().size(), is(1));
        assertThat(savedOrganizer.getEvents().size(), is(1));
        assertThat(savedOrganizer.getAdverts().contains(advert), is(true));
        assertThat(savedOrganizer.getEvents().contains(event), is(true));
    }
}
