package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.entities.Advert;
import app.entities.Event;
import app.enums.AddPlacement;
import app.enums.EventCategory;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvertDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private AdvertDAO advertDAO;
    private EventDAO eventDAO;
    private AddressDAO addressDAO;
    private Map<String, Advert> adverts;

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        adverts = TestDataCreator.createAdverts(emf);
    }

    @BeforeAll
    void setUpAll() {
        advertDAO = new AdvertDAO(emf);
        eventDAO = new EventDAO(emf);
        addressDAO = new AddressDAO(emf);
    }

    @Test
    void create() {
        Address address = Address.builder()
                .postalCode("2100")
                .city("København")
                .address("Testvej 1")
                .build();
        Address createdAddress = addressDAO.create(address);

        Event event = Event.builder()
                .title("Test Event")
                .category(EventCategory.MUSIC)
                .location(createdAddress)
                .startDate(LocalDate.now().plusDays(1))
                .build();
        Event createdEvent = eventDAO.create(event);

        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .event(createdEvent)
                .build();

        Advert advertCreated = advertDAO.create(advert);
        assertThat(advertCreated.getAdvertId(), notNullValue());

        Advert advertFetched = advertDAO.readById(advertCreated.getAdvertId());
        assertThat(advertFetched.getAddPlacement().getPrice(), equalTo(500.00));
        assertThat(advertFetched.getAdvertId(), is(advertCreated.getAdvertId()));
    }

    @Test
    void read() {
        Advert advert = adverts.get("advert");
        Advert advertFetched = advertDAO.readById(advert.getAdvertId());
        assertThat(advertFetched.getAddPlacement().getPrice(), equalTo(300.00));
        assertThat(advertFetched.getAdvertId(), is(advert.getAdvertId()));
    }

    @Test
    void readAll() {
        List<Advert> allAdverts = advertDAO.readAll();
        assertThat(allAdverts, hasSize(2));
        assertThat(allAdverts, containsInAnyOrder(adverts.values().toArray()));
    }

    @Test
    void update() {
        Advert advert = adverts.get("advert");
        Advert advertToUpdate = Advert.builder()
                .advertId(advert.getAdvertId())
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .startDate(LocalDate.of(2026, 5, 5))
                .endDate(LocalDate.of(2026, 7, 7))
                .event(advert.getEvent())
                .build();

        Advert fetchedAdvert = advertDAO.update(advertToUpdate);

        assertThat(fetchedAdvert.getAdvertId(), is(advert.getAdvertId()));
        assertThat(fetchedAdvert.getAddPlacement().getPrice(), is(500.00));
    }

    @Test
    void delete() {
        Advert advert = adverts.get("advert");

        boolean deletedAdvert = advertDAO.delete(advert);
        assertThat(deletedAdvert, is(true));
        assertThrows(DatabaseIdException.class, () -> advertDAO.readById(advert.getAdvertId()));
    }

    @Test
    void create_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.create(null));
        assertThat(ex.getMessage(), is("Advert is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.readById(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.readById(999_999));
        assertThat(ex.getMessage(), is("Advert not found with id: 999999"));
    }

    @Test
    void update_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.update(null));
        assertThat(ex.getMessage(), is("Advert is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        Advert missing = Advert.builder()
                .advertId(999_999)
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .build();

        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.update(missing));
        assertThat(ex.getMessage(), is("Advert not found with id: " + missing.getAdvertId()));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.delete(Advert.builder().addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT).build()));
        assertThat(ex.getMessage(), is("Advert id is required for deleting"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Advert missing = Advert.builder()
                .advertId(999_999)
                .addPlacement(AddPlacement.FRONTPAGE_HIGHLIGHT)
                .build();

        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> advertDAO.delete(missing));
        assertThat(ex.getMessage(), is("Advert not found with id: " + missing.getAdvertId()));
    }
}