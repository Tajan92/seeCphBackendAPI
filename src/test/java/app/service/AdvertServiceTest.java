package app.service;

import app.config.HibernateTestConfig;
import app.dao.AdvertDAO;
import app.dao.AddressDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.exceptions.DatabaseIdException;
import app.utils.TestDataCreator;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvertServiceTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private AdvertService advertService;
    private AdvertDAO advertDAO;
    private UserDAO userDAO;
    private EventDAO eventDAO;
    private AddressDAO addressDAO;
    private UserService userService;
    private EventService eventService;
    private AddressService addressService;
    private Map<String, User> users;
    private Map<String, Event> events;
    private Map<String, Advert> adverts;
    private int userId;
    private int eventId;

    @BeforeAll
    void setUpAll() {
        advertDAO = new AdvertDAO(emf);
        userDAO = new UserDAO(emf);
        eventDAO = new EventDAO(emf);
        addressDAO = new AddressDAO(emf);
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        users = TestDataCreator.createUsers(emf);
        events = TestDataCreator.createEvents(emf);
        adverts = TestDataCreator.createAdverts(emf);
        userService = new UserService(userDAO);
        addressService = new AddressService(addressDAO);
        eventService = new EventService(eventDAO, addressService, userService);
        advertService = new AdvertService(advertDAO, userDAO, eventDAO, eventService, userService);

        userId = users.get("admin").getId();
        eventId = events.values().iterator().next().getId();
    }

    @Test
    public void create() {
        AdvertDTORequest request = new AdvertDTORequest(
                eventId,
                userId,
                AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 15)
        );

        AdvertDTOResponse response = advertService.create(request);

        assertNotNull(response);
        assertThat(response.addPlacement(), is(AddPlacement.FRONTPAGE_HEADER));
        assertThat(response.startDate(), is(LocalDate.of(2026, 11, 1)));
        assertThat(response.endDate(), is(LocalDate.of(2026, 11, 15)));
    }

    @Test
    public void getById() {
        AdvertDTORequest request = new AdvertDTORequest(
                eventId,
                userId,
                AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 10)
        );

        AdvertDTOResponse created = advertService.create(request);
        AdvertDTOResponse fetched = advertService.getById(created.id());

        assertNotNull(fetched);
        assertThat(fetched.id(), is(created.id()));
        assertThat(fetched.addPlacement(), is(AddPlacement.FRONTPAGE_HEADER));
    }

    @Test
    public void getAll() {
        List<AdvertDTOResponse> allAdverts = advertService.getAll();

        assertNotNull(allAdverts);
        assertThat(allAdverts.size(), is(not(0)));
    }

    @Test
    public void updateById() {
        AdvertDTORequest initialRequest = new AdvertDTORequest(
                eventId,
                userId,
                AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 5)
        );

        AdvertDTOResponse created = advertService.create(initialRequest);

        AdvertDTORequest updatedRequest = new AdvertDTORequest(
                eventId,
                userId,
                AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 10)
        );

        AdvertDTOResponse updated = advertService.updateById(created.id(), updatedRequest);

        assertNotNull(updated);
        assertThat(updated.addPlacement(), is(AddPlacement.FRONTPAGE_HEADER));
        assertThat(updated.startDate(), is(LocalDate.of(2026, 11, 2)));
        assertThat(updated.endDate(), is(LocalDate.of(2026, 11, 10)));
    }

    @Test
    public void deleteById() {
        AdvertDTORequest request = new AdvertDTORequest(
                eventId,
                userId,
                AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 5)
        );

        AdvertDTOResponse created = advertService.create(request);
        int advertId = created.id();

        boolean isDeleted = advertService.deleteById(advertId);
        assertThat(isDeleted, is(true));

        assertThrows(DatabaseIdException.class, () -> {advertService.getById(advertId);});
    }
}