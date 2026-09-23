package app.service;

import app.config.HibernateTestConfig;
import app.dao.EventDAO;
import app.entities.Event;
import app.entities.users.User;
import app.utils.TestDataCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private ObjectMapper objectMapper;
    private EventService eventService;
    private EventDAO eventDAO;
    private Map<String, Event> events;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        eventService = new EventService(eventDAO);
    }

    @BeforeAll
    void setUpAll() {
        eventDAO = new EventDAO(emf);
    }

    @Test
    void persistEvents() {
    }
}