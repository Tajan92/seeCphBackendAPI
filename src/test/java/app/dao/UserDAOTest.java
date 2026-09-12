package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Advert;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.enums.Status;
import app.enums.UserRole;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private UserDAO userDAO;
    private Map<String, User> users;

    @BeforeEach
    void setUp() {
        users = TestDataCreator.createUsers(emf);
    }

    @BeforeAll
    void setUpAll() {
        userDAO = new UserDAO(emf);
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
        assertThat(attendeeFetched.getUserId(), is(16));
    }

    @Test
    void createOrganizer() {
        User organizer = Organizer.builder().organizerName("Maersk").accountStatus(Status.PENDING).name("Maersk").email("maersk@mail.dk").phone("67890123").password("67890123").userRole(UserRole.ORGANIZER).build();
        User organizerCreated = userDAO.create(organizer);

        assertThat(organizerCreated.getUserId(), notNullValue());

        User organizerFetched = userDAO.read(organizerCreated.getUserId());
        assertThat(organizerFetched.getName(), is("Maersk"));
        assertThat(organizerFetched.getUserId(), is(16));
    }

    @Test
    void read() {
        User user = users.get("attendee4");
        User fetched = userDAO.read(user.getUserId());
        assertThat(fetched.getUserId(), is(user.getUserId()));
        assertThat(fetched.getName(), is(user.getName()));
    }

    @Test
    void readAll() {
        Set<User> allUsers = userDAO.readAll();
        assertThat(allUsers, hasSize(15));
        assertThat(allUsers, containsInAnyOrder(users.values().toArray()));
    }

    @Test
    void updateAdmin() {
        User admin = users.get("admin");
        admin.setName("newName");

        User fetchedUpdated = userDAO.update(admin);

        assertThat(fetchedUpdated.getUserId(), is(admin.getUserId()));
        assertThat(fetchedUpdated.getName(), is("newName"));
        assertThat(fetchedUpdated.getUserId(), is(admin.getUserId()));
    }

    @Test
    void updateAttendee() {
        User attendee = users.get("attendee");
        attendee.setName("newName");

        User fetchedUpdated = userDAO.update(attendee);

        assertThat(fetchedUpdated.getUserId(), is(attendee.getUserId()));
        assertThat(fetchedUpdated.getName(), is("newName"));
        assertThat(fetchedUpdated.getUserId(), is(attendee.getUserId()));
    }

    @Test
    void updateOrganizer() {
        User organizer = users.get("organizer");
        organizer.setName("newName");

        User fetchedUpdated = userDAO.update(organizer);

        assertThat(fetchedUpdated.getUserId(), is(organizer.getUserId()));
        assertThat(fetchedUpdated.getName(), is("newName"));
        assertThat(fetchedUpdated.getUserId(), is(organizer.getUserId()));
    }

    @Test
    void delete() {
        User admin = users.get("admin");
        User attendee = users.get("attendee");
        User organizer = users.get("organizer");

        boolean deletedAdmin = userDAO.delete(admin);
        boolean deletedAttendee = userDAO.delete(attendee);
        boolean deletedOrganizer = userDAO.delete(organizer);

        assertThat(deletedAdmin, is(true));
        assertThat(deletedAttendee, is(true));
        assertThat(deletedOrganizer, is(true));
        assertThrows(DatabaseException.class, () -> userDAO.read(admin.getUserId()));
        assertThrows(DatabaseException.class, () -> userDAO.read(attendee.getUserId()));
        assertThrows(DatabaseException.class, () -> userDAO.read(organizer.getUserId()));
    }

    @Test
    void create_withNullUser_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.create(null));
        assertThat(ex.getMessage(), is("User is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.read(999_999));
        assertThat(ex.getMessage(), is("User not found with id: 999999"));
    }

    @Test
    void update_withNullUser_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.update(null));
        assertThat(ex.getMessage(), is("User is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        User missing = Admin.builder()
                .name("Missing")
                .userId(999_999)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.update(missing));
        assertThat(ex.getMessage(), is("Updating User failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.delete(null));
        assertThat(ex.getMessage(), is("User is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        User missing = Admin.builder()
                .name("Missing")
                .userId(999_999)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> userDAO.delete(missing));
        assertThat(ex.getMessage(), is("Delete User failed"));
    }
}