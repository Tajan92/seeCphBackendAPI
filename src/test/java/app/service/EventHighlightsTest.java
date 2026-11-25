package app.service;

import app.config.HibernateTestConfig;
import app.dao.AddressDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.event.EventDTOResponse;
import app.entities.Address;
import app.entities.Event;
import app.enums.EventCategory;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventHighlightsTest {
    private EntityManagerFactory emf;
    private EventService eventService;
    private Map<String, Event> events;

    @BeforeAll
    void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        eventService = new EventService(new EventDAO(emf), new AddressService(new AddressDAO(emf)), new UserDAO(emf));
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        events = TestDataCreator.createHighlightEvents(emf);
    }

    private List<String> titles(List<EventDTOResponse> highlights) {
        return highlights.stream().map(EventDTOResponse::title).toList();
    }

    @Test
    void returnsNineEvents() {
        List<EventDTOResponse> highlights = eventService.getHighlights();

        assertThat(highlights, hasSize(9));
    }

    @Test
    void paidHighlightsComeFirstSortedByDate() {
        List<String> titles = titles(eventService.getHighlights());

        assertThat(titles.get(0), is("Highlighted Event"));       // in 3 days
        assertThat(titles.get(1), is("Highlighted Far Event"));   // in 20 days
    }

    @Test
    void paidHighlightIsShownEvenOutsideTheFourteenDays() {
        List<String> titles = titles(eventService.getHighlights());

        assertThat(titles, hasItem("Highlighted Far Event"));
    }

    @Test
    void expiredAndOtherPlacementAdvertsAreNotPrioritized() {
        List<String> firstTwo = titles(eventService.getHighlights()).subList(0, 2);

        assertThat(firstTwo, not(hasItem("Expired Advert Event")));
        assertThat(firstTwo, not(hasItem("Header Advert Event")));
    }

    @Test
    void noEventIsShownTwice() {
        List<Integer> ids = eventService.getHighlights().stream().map(EventDTOResponse::id).toList();

        assertThat(ids.stream().distinct().count(), is((long) ids.size()));
    }

    @Test
    void neverShowsPastEventsOrUnpaidEventsAfterFourteenDays() {
        List<String> titles = titles(eventService.getHighlights());

        assertThat(titles, not(hasItem("Past Event")));
        assertThat(titles, not(hasItem("Far Future Event")));
    }

    @Test
    void fillEventsAreWithinTheNextFourteenDays() {
        LocalDate today = LocalDate.now();
        List<EventDTOResponse> fill = eventService.getHighlights().subList(2, 9); // after the 2 paid highlights

        for (EventDTOResponse event : fill) {
            assertThat(event.startDate(), allOf(
                    greaterThanOrEqualTo(today),
                    lessThanOrEqualTo(today.plusDays(14))));
        }
    }

    @Test
    void sameSelectionWhenCalledAgainInTheSameWeek() {
        List<Integer> first = eventService.getHighlights().stream().map(EventDTOResponse::id).toList();
        List<Integer> second = eventService.getHighlights().stream().map(EventDTOResponse::id).toList();

        assertThat(second, is(first));
    }

    @Test
    void returnsFewerThanNineWhenThereAreNotEnoughEvents() {
        TestDataCreator.clearDatabase(emf);
        createEvents(3);

        assertThat(eventService.getHighlights(), hasSize(3));
    }

    @Test
    void returnsEmptyListWhenThereAreNoEvents() {
        TestDataCreator.clearDatabase(emf);

        assertThat(eventService.getHighlights(), is(empty()));
    }

    private void createEvents(int count) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Address address = Address.builder().postalCode("2100").city("København Ø").address("Testvej 1").build();
            em.persist(address);
            for (int i = 1; i <= count; i++) {
                em.persist(Event.builder()
                        .title("Few " + i)
                        .category(EventCategory.MUSIC)
                        .startDate(LocalDate.now().plusDays(i))
                        .price(100.0)
                        .location(address)
                        .build());
            }
            em.getTransaction().commit();
        }
    }
}