package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.enums.AddPlacement;
import app.enums.EventCategory;
import app.enums.UserRole;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private UserDAO userDAO;
    private AdvertDAO advertDAO;
    private EventDAO eventDAO;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;
    private Admin admin;

    @BeforeEach
    void setUp() {
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
        admin = Admin.builder()
                .name("RF")
                .email("runfun@mail.dk")
                .password("12345678")
                .userRole(UserRole.ADMIN)
                .phone("89765432")
                .build();
    }

    @BeforeAll
    void setUpAll() {
        userDAO = new UserDAO(emf);
        eventDAO = new EventDAO(emf);
        advertDAO = new AdvertDAO(emf);
    }

    @Test
    void createAdminUpdateWithEventAndAdvert() {
        Admin fetchedAdmin = (Admin) userDAO.create(admin);

        fetchedAdmin.addAdvert(adverts.get("advert"));
        fetchedAdmin.addAdvert(adverts.get("advert2"));

        fetchedAdmin.addEvent(events.get("event"));
        fetchedAdmin.addEvent(events.get("event2"));

        Admin updatedAdmin = (Admin) userDAO.update(fetchedAdmin);
        assertThat(updatedAdmin.getAdverts().size(), is(2));
        assertThat(updatedAdmin.getEvents().size(), is(2));
    }

    @Test
    void createAdminWithCascadePersist() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(150.00)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .build();

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build())
                .build();

        admin.addAdvert(advert);
        admin.addEvent(event);

        Admin savedAdmin = (Admin) userDAO.create(admin);

        assertThat(event.getAdmin(), is(savedAdmin));
        assertThat(savedAdmin.getUserId(), notNullValue());
        assertThat(savedAdmin.getAdverts().size(), is(1));
        assertThat(savedAdmin.getEvents().size(), is(1));
        assertThat(savedAdmin.getAdverts().contains(advert), is(true));
        assertThat(savedAdmin.getEvents().contains(event), is(true));
    }

    @Test
    void deleteAdmin() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(150.00)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .build();

        admin.addAdvert(advert);

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build())
                .build();

        admin.addEvent(event);

        Admin createdAdmin = (Admin) userDAO.create(admin);
        Integer advertId = createdAdmin.getAdverts().iterator().next().getAdvertId();
        Integer eventId = createdAdmin.getEvents().iterator().next().getEventId();
        Advert fetchedAdvert = advertDAO.readById(advertId);
        assertThat(fetchedAdvert, is(advert));

        Event fetchedEvent = eventDAO.readById(eventId);
        assertThat(fetchedEvent, is(event));

        boolean deleted = userDAO.delete(createdAdmin);
        assertThat(deleted, is(true));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.readById(advertId));
        assertThat(ex.getMessage(), is("Advert not found with id: "+advertId));

        DatabaseException ex2 = assertThrows(DatabaseException.class, () -> eventDAO.readById(eventId));
        assertThat(ex2.getMessage(), is("Event not found with id: "+eventId));
    }

    @Test
    void deleteAdvertAndEvent() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(150.00)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .build();

        admin.addAdvert(advert);

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build())
                .build();

        admin.addEvent(event);

        Admin createdAdmin = (Admin) userDAO.create(admin);

        Integer advertId = createdAdmin.getAdverts().iterator().next().getAdvertId();
        Integer eventId = createdAdmin.getEvents().iterator().next().getEventId();
        Advert fetchedAdvert = advertDAO.readById(advertId);
        Event fetchedEvent = eventDAO.readById(eventId);

        createdAdmin.getAdverts().remove(fetchedAdvert);
        createdAdmin.getEvents().remove(fetchedEvent);

        Admin updatedAdmin = (Admin) userDAO.update(admin);
        assertThat(updatedAdmin.getAdverts().size(), is(0));
        assertThat(updatedAdmin.getEvents().size(), is(0));
    }
}
