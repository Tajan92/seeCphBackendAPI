package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdvertIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private AdvertDAO advertDAO;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;
    private Map<String, User> users;


    @BeforeEach
    void setUp() {
        users = TestDataCreator.createUsers(emf);
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
    }

    @BeforeAll
    void setUpAll() {
        advertDAO = new AdvertDAO(emf);
    }

    @Test
    void checkPrePersistStatusTrue() {
       Advert advert = Advert.builder()
               .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
               .price(500.00)
               .startDate(LocalDate.now().minusDays(10))
               .endDate(LocalDate.now().plusDays(10))
               .build();

       Advert createdAdvert = advertDAO.create(advert);
       assertThat(createdAdvert.isStatus(), is(true));
    }

    @Test
    void checkPrePersistStatusFalse() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(500.00)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(10))
                .build();

        Advert createdAdvert = advertDAO.create(advert);
        assertThat(createdAdvert.isStatus(), is(false));
    }

    @Test
    void checkPreUpdateStatusTrue() {
        Advert advert = adverts.get("advert");
        assertThat(advert.isStatus(), is(false));

        advert.setStartDate(LocalDate.now().minusDays(10));
        advert.setEndDate(LocalDate.now().plusDays(10));

        Advert updatedAdvert = advertDAO.update(advert);

        assertThat(updatedAdvert.isStatus(), is(true));
    }

    @Test
    void checkPreUpdateStatusFalse() {
        Advert advert = adverts.get("advert2");
        assertThat(advert.isStatus(), is(true));

        advert.setStartDate(LocalDate.now().plusDays(10));
        advert.setEndDate(LocalDate.now().plusDays(20));

        Advert updatedAdvert = advertDAO.update(advert);

        assertThat(updatedAdvert.isStatus(), is(false));
    }

    @Test
    void CheckRelations() {
        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(500.00)
                .startDate(LocalDate.of(2026, 11, 15))
                .endDate(LocalDate.of(2026, 11, 22))
                .build();

        Advert savedAdvert = advertDAO.create(advert);
        Event event = events.get("event");
        Organizer organizer = (Organizer) users.get("organizer");
        Admin admin = (Admin) users.get("admin");

        savedAdvert.setEvent(event);
        savedAdvert.setOrganizer(organizer);
        savedAdvert.setAdmin(admin);
        Advert updatedAdvert = advertDAO.update(savedAdvert);

        assertThat(updatedAdvert.getEvent(), is(event));
        assertThat(updatedAdvert.getOrganizer(), is(organizer));
        assertThat(updatedAdvert.getAdmin(), is(admin));
    }
}
