package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.Event;
import app.enums.AddPlacement;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvertDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private AdvertDAO advertDAO;
    private Map<String, Advert> adverts;

    @BeforeEach
    void setUp() {
        adverts = TestDataCreator.createAdverts(emf);
    }

    @BeforeAll
    void setUpAll() {
        advertDAO = new  AdvertDAO(emf);
    }

    @Test
    void create() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(150.00)
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 10, 31))
                .build();

        Advert advertCreated = advertDAO.create(advert);
        assertThat(advertCreated.getAdvertId(), notNullValue());

        Advert advertFetched = advertDAO.read(advertCreated.getAdvertId());
        assertThat(advertFetched.getPrice(), equalTo(150.00));
        assertThat(advertFetched.getAdvertId(), is(3));
    }

    @Test
    void read() {
        Advert advert = adverts.get("advert");
        Advert advertFetched = advertDAO.read(advert.getAdvertId());
        assertThat(advertFetched.getPrice(), equalTo(205.00));
        assertThat(advertFetched.getAdvertId(), is(advert.getAdvertId()));
    }

    @Test
    void readAll() {
        Set<Advert> allAdverts = advertDAO.readAll();
        assertThat(allAdverts, hasSize(2));
        assertThat(allAdverts, containsInAnyOrder(adverts.values().toArray()));
    }

    @Test
    void update() {
        Advert advert = adverts.get("advert");
        advert.setPrice(50.00);

        Advert fetchedAdvert = advertDAO.update(advert);

        assertThat(fetchedAdvert.getAdvertId(), is(advert.getAdvertId()));
        assertThat(fetchedAdvert.getPrice(), is(50.00));
        assertThat(fetchedAdvert.getAdvertId(), is(advert.getAdvertId()));
    }

    @Test
    void delete() {
        Advert advert = adverts.get("advert");

        boolean deletedAdvert = advertDAO.delete(advert);
        assertThat(deletedAdvert, is(true));
        assertThrows(DatabaseException.class, () -> advertDAO.read(advert.getAdvertId()));
    }

    @Test
    void create_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.create(null));
        assertThat(ex.getMessage(), is("Advert is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.read(999_999));
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
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.update(missing));
        assertThat(ex.getMessage(), is("Updating Advert failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.delete(null));
        assertThat(ex.getMessage(), is("Advert is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Advert missing = Advert.builder()
                .advertId(999_999)
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> advertDAO.delete(missing));
        assertThat(ex.getMessage(), is("Delete Advert failed"));
    }
}