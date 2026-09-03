package app.daos;

import app.config.HibernateTestConfig;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.Status;
import app.enums.UserRole;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private UserDAO userDAO;
    private Map<String, User> users;

    @BeforeAll
    void setUp() {
        userDAO = new UserDAO(emf);
        users = TestDataCreator.createUsers(emf);
    }

    @AfterAll
    void tearDown() {
        emf.close();
    }

    @Test
    void createAdmin() {
        User admin = Admin.builder().name("Camilla").email("camilla@mail.dk").phone("67890123").password("67890123").userRole(UserRole.ADMIN).build();
        User adminCreated = userDAO.create(admin);

        assertThat(adminCreated.getUserId(), notNullValue());

        User adminFetched = userDAO.read(adminCreated.getUserId());
        assertThat(adminFetched.getName(), is("Camilla"));
        assertThat(adminFetched.getUserId(), is(16));
    }
    @Test
    void createAttendee() {
        User attendee = Attendee.builder().name("Peter").email("peter@mail.dk").phone("67890123").password("67890123").userRole(UserRole.ATTENDEE).build();
        User attendeeCreated = userDAO.create(attendee);

        assertThat(attendeeCreated.getUserId(), notNullValue());

        User attendeeFetched = userDAO.read(attendeeCreated.getUserId());
        assertThat(attendeeFetched.getName(), is("Peter"));
        assertThat(attendeeFetched.getUserId(), is(17));
    }

    @Test
    void createOrganizer() {
        User organizer = Organizer.builder().organizerName("Maersk").accountStatus(Status.PENDING).name("Maersk").email("maersk@mail.dk").phone("67890123").password("67890123").userRole(UserRole.ORGANIZER).build();
        User organizerCreated = userDAO.create(organizer);

        assertThat(organizerCreated.getUserId(), notNullValue());

        User organizerFetched = userDAO.read(organizerCreated.getUserId());
        assertThat(organizerFetched.getName(), is("Maersk"));
        assertThat(organizerFetched.getUserId(), is(18));
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