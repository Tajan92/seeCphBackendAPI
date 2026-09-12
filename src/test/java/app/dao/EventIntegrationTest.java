package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.Event;
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
public class EventIntegrationTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private EventDAO eventDAO;
    private AdvertDAO advertDAO;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;

    @BeforeEach
    void setUp() {
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
    }

    @BeforeAll
    void setUpAll() {
        eventDAO = new EventDAO(emf);
        advertDAO = new AdvertDAO(emf);
    }

    @Test
    void createEventAddAdvertThenDeleteEvent() {
        Event event = events.get("event");

        Advert advert = Advert.builder()
                .addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT)
                .price(500.00)
                .startDate(LocalDate.of(2026, 11, 15))
                .endDate(LocalDate.of(2026, 11, 22))
                .build();

        event.addAdvert(advert);
        Event updatedEvent = eventDAO.update(event);

        assertThat(updatedEvent.getAdverts().size(), is(1));

        Advert addedAdvert = updatedEvent.getAdverts().iterator().next();
        updatedEvent.getAdverts().remove(addedAdvert);

        Event eventAfterDelete = eventDAO.update(updatedEvent);

        assertThat(eventAfterDelete.getAdverts().size(), is(0));
        assertThat(advertDAO.readAll().size(), is(adverts.size()));
    }
}
