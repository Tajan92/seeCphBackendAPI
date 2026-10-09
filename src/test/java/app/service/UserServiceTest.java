package app.service;

import app.config.HibernateTestConfig;
import app.dao.UserDAO;
import app.dto.user.OrganizerDTOResponse;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.enums.Status;
import app.enums.UserRole;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private EntityManagerFactory emf;
    private UserDAO userDAO;
    private UserService userService;

    @BeforeAll
    void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        userDAO = new UserDAO(emf);
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    public void create() {
        UserDTORequest request = new UserDTORequest(
                "Test Attendee",
                "attendee@test.dk",
                "12345678",
                "password123",
                null
        );

        UserDTOResponse response = userService.create(request);

        assertNotNull(response);
        assertThat(response.name(), is("Test Attendee"));
        assertThat(response.email(), is("attendee@test.dk"));
        assertThat(response.userRole(), is(UserRole.ATTENDEE));
    }

    @Test
    public void createPendingOrganizer() {
        UserDTORequest request = new UserDTORequest(
                "Nike Store",
                "nike@test.dk",
                "87654321",
                "password123",
                "Nike"
        );

        OrganizerDTOResponse response = userService.createPendingOrganizer(request);

        assertNotNull(response);
        assertThat(response.name(), is("Nike Store"));
        assertThat(response.userRole(), is(UserRole.ORGANIZER));
        assertThat(response.status(), is(Status.PENDING));
    }

    @Test
    public void createAdmin() {
        UserDTORequest request = new UserDTORequest(
                "Super Admin",
                "admin@test.dk",
                "11223344",
                "securepassword",
                null
        );

        UserDTOResponse response = userService.createAdmin(request);

        assertNotNull(response);
        assertThat(response.name(), is("Super Admin"));
        assertThat(response.userRole(), is(UserRole.ADMIN));
    }

    @Test
    public void getById() {
        UserDTORequest request = new UserDTORequest(
                "Fetch User",
                "fetch@test.dk",
                "99887766",
                "password123",
                null
        );

        UserDTOResponse created = userService.create(request);
        UserDTOResponse fetched = userService.getById(created.id());

        assertNotNull(fetched);
        assertThat(fetched.id(), is(created.id()));
        assertThat(fetched.email(), is("fetch@test.dk"));
    }

    @Test
    public void getAll() {
        // Opret en bruger først for at sikre, at listen ikke er tom
        UserDTORequest request = new UserDTORequest(
                "List User",
                "list@test.dk",
                "12341234",
                "password123",
                null
        );
        userService.create(request);

        List<UserDTOResponse> allUsers = userService.getAll();

        assertNotNull(allUsers);
        assertThat(allUsers.size(), is(not(0)));
    }

    @Test
    public void updateById() {
        UserDTORequest initialRequest = new UserDTORequest(
                "Old Name",
                "update@test.dk",
                "12345678",
                "password123",
                null
        );

        UserDTOResponse created = userService.create(initialRequest);

        assertThat(created.name(), is("Old Name"));
        assertThat(created.phone(), is("12345678"));

        UserDTORequest updateRequest = new UserDTORequest(
                "New Name",
                "update@test.dk",
                "87654321",
                "newpassword",
                null
        );

        UserDTOResponse updated = userService.updateById(created.id(), updateRequest);

        assertNotNull(updated);
        assertThat(updated.name(), is("New Name"));
        assertThat(updated.phone(), is("87654321"));
    }

    @Test
    public void deleteById() {
        UserDTORequest request = new UserDTORequest(
                "Delete User",
                "delete@test.dk",
                "12345678",
                "password123",
                null
        );

        UserDTOResponse created = userService.create(request);
        int userId = created.id();

        boolean isDeleted = userService.deleteById(userId);
        assertThat(isDeleted, is(true));

        assertThrows(DatabaseIdException.class, () -> {
            userService.getById(userId);
        });
    }
}