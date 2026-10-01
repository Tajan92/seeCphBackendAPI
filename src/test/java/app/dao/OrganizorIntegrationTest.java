package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Organizer;
import app.enums.*;
import app.exceptions.DatabaseIdException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrganizorIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private UserDAO userDAO;
    private AdvertDAO advertDAO;
    private EventDAO eventDAO;
    private AddressDAO addressDAO;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
        organizer = Organizer.builder()
                .organizerName("Run and Fun")
                .name("RF")
                .email("runfun@mail.dk")
                .password("12345678")
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
        addressDAO = new AddressDAO(emf);
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
        Address address = Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build();
        Address createdAddress = addressDAO.create(address);

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(createdAddress)
                .build();

        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .event(event)
                .build();

        organizer.addAdvert(advert);
        organizer.addEvent(event);

        Organizer savedOrganizer = (Organizer) userDAO.create(organizer);

        assertThat(event.getOrganizer(), is(savedOrganizer));
        assertThat(savedOrganizer.getUserId(), notNullValue());
        assertThat(savedOrganizer.getAdverts().size(), is(1));
        assertThat(savedOrganizer.getEvents().size(), is(1));
        assertThat(savedOrganizer.getAdverts().contains(advert), is(true));
        assertThat(savedOrganizer.getEvents().contains(event), is(true));
    }

    @Test
    void deleteOrganizer() {
        Address address = Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build();
        Address createdAddress = addressDAO.create(address);

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(createdAddress)
                .build();

        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .event(event)
                .build();

        organizer.addAdvert(advert);
        organizer.addEvent(event);

        Organizer createdOrganizer = (Organizer) userDAO.create(organizer);

        Integer advertId = createdOrganizer.getAdverts().iterator().next().getAdvertId();
        Integer eventId = createdOrganizer.getEvents().iterator().next().getEventId();

        Advert fetchedAdvert = advertDAO.readById(advertId);
        assertThat(fetchedAdvert, notNullValue());

        Event fetchedEvent = eventDAO.readById(eventId);
        assertThat(fetchedEvent, notNullValue());

        boolean deleted = userDAO.delete(createdOrganizer);
        assertThat(deleted, is(true));

        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.readById(advertId));
        assertThat(ex.getMessage(), is("Advert not found with id: "+advertId));

        DatabaseIdException ex2 = assertThrows(DatabaseIdException.class, () -> eventDAO.readById(eventId));
        assertThat(ex2.getMessage(), is("Event not found with id: "+eventId));
    }

    @Test
    void deleteAdvertAndEvent() {
        Address address = Address.builder().postalCode("1200").city("København").address("Frederiksberg allé").build();
        Address createdAddress = addressDAO.create(address);

        Event event = Event.builder()
                .title("Copenhagen Marathon")
                .description("Run through Copenhagen with busy streets")
                .category(EventCategory.ATHLETIC_RACES)
                .startTime(LocalTime.of(16, 0))
                .startDate(LocalDate.of(2026, 10, 18))
                .price(100.00)
                .location(createdAddress)
                .build();

        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .event(event)
                .build();

        organizer.addAdvert(advert);
        organizer.addEvent(event);

        Organizer createdOrganizer = (Organizer) userDAO.create(organizer);

        Integer advertId = createdOrganizer.getAdverts().iterator().next().getAdvertId();
        Integer eventId = createdOrganizer.getEvents().iterator().next().getEventId();
        Advert fetchedAdvert = advertDAO.readById(advertId);
        Event fetchedEvent = eventDAO.readById(eventId);

        createdOrganizer.getAdverts().remove(fetchedAdvert);
        createdOrganizer.getEvents().remove(fetchedEvent);

        Organizer updatedOrganizer = (Organizer) userDAO.update(createdOrganizer);
        assertThat(updatedOrganizer.getAdverts().size(), is(0));
        assertThat(updatedOrganizer.getEvents().size(), is(0));
    }
}