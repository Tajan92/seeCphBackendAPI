package app.service;

import app.config.HibernateTestConfig;
import app.dao.AddressDAO;
import app.dao.EventDAO;
import app.entities.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import java.util.Map;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private ObjectMapper objectMapper;
    private EventService eventService;
    private EventDAO eventDAO;
    private AddressDAO addressDAO;
    private AddressService addressService;
    private Map<String, Event> events;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        addressService = new AddressService(addressDAO);
        eventService = new EventService(eventDAO, addressService);
    }

    @BeforeAll
    void setUpAll() {
        eventDAO = new EventDAO(emf);
        addressDAO = new AddressDAO(emf);
    }


}