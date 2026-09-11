package app.daos;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.users.User;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Map;

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

    @AfterAll
    void tearDown() {
        emf.close();
    }

    @Test
    void create() {
    }

    @Test
    void read() {
    }

    @Test
    void readAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}