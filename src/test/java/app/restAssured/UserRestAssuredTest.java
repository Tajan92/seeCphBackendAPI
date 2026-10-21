package app.restAssured;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.entities.Event;
import app.entities.users.User;
import app.utils.TestDataCreator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRestAssuredTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static ApplicationConfig applicationConfig;

    private Map<String, Event> eventMap;
    private Map<String, User> userMap;

    private String adminToken;
    private String organizerToken;
    private String attendeeToken;

    @BeforeAll
    void init() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        applicationConfig = new ApplicationConfig(emf);
        app = applicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api/v1";
        resetData();
        adminToken = login(userMap.get("admin").getEmail(), "12345678");
        organizerToken = login(userMap.get("organizer2").getEmail(), "12345678");
        attendeeToken = login(userMap.get("attendee").getEmail(), "12345678");
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);


    }

    @AfterEach
    void tearDown() {
        eventMap.clear();
        userMap.clear();
    }

    @AfterAll
    static void shutDown() {
        applicationConfig.stopServer(app);
    }

    private String login(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password))
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    void loginReturnsTokenAndUser() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "bo@mail.dk", "password", "12345678"))
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("user.email", equalTo("bo@mail.dk"))
                .body("user.userRole", equalTo("ATTENDEE"));
    }

    @Test
    void loginWithWrongPasswordGives401() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "bo@mail.dk", "password", "wrong-password"))
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("Invalid user or password"));
    }

    @Test
    void loginWithUnknownEmailGives401() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "nobody@mail.dk", "password", "12345678"))
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    void loginAsPendingOrganizerGives403() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "nike@mail.dk", "password", "12345678"))
                .when()
                .post("/login")
                .then()
                .statusCode(403)
                .body("error", equalTo("Organizer account is not active"));
    }

    @Test
    void changePassword() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("email","adidas@mail.dk",
                        "password", "12345678",
                        "newPassword", "NewPassword1#",
                        "newPasswordCheck", "NewPassword1#"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(204);
    }

    @Test
    void changePasswordNewPasswordMismatchGives400() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("email","adidas@mail.dk",
                        "password", "12345678",
                        "newPassword", "NewPassword1#",
                        "newPasswordCheck", "NewWrongPassword1#"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(400);
    }

    @Test
    void changePasswordPasswordNotValidGives400() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("email","adidas@mail.dk",
                        "password", "12345678",
                        "newPassword", "Password",
                        "newPasswordCheck", "Password"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(400);
    }

    @Test
    void changePasswordWrongPasswordGives401() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("email","adidas@mail.dk",
                        "password", "Wrong",
                        "newPassword", "NewPassword1#",
                        "newPasswordCheck", "NewPassword1#"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(401);
    }

    @Test
    void changePasswordNoAccessToUserGives403() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("email", "12345678", "newPassword1#", "newPassword1#"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(400);
    }

    @Test
    void changePasswordWithoutTokenGives401() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "12345678", "newPassword1#", "newPassword1#"))
                .when()
                .put("/users/my/password")
                .then()
                .statusCode(401);
    }

    @Test
    void createAttendee() {
        given()
                .contentType(ContentType.JSON)
                .body(newUser("new@mail.dk"))
                .when()
                .post("/users/attendee")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("email", equalTo("new@mail.dk"))
                .body("userRole", equalTo("ATTENDEE"));
    }

    @Test
    void createAttendeeWithExistingEmailGives409() {
        given()
                .contentType(ContentType.JSON)
                .body(newUser("bo@mail.dk"))
                .when()
                .post("/users/attendee")
                .then()
                .statusCode(409)
                .body("error", equalTo("Email is already in use"));
    }

    @Test
    void createAttendeeWithWeakPasswordGives400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "Weak Password",
                        "email", "weak@mail.dk",
                        "phone", "87654321",
                        "password", "abc",
                        "passwordCheck", "abc"))
                .when()
                .post("/users/attendee")
                .then()
                .statusCode(400)
                .body("error", containsString("Password must be at least 8 characters long"));
    }

    @Test
    void createAttendeeWithMismatchingPasswordsGives400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "Mismatch",
                        "email", "mismatch@mail.dk",
                        "phone", "87654321",
                        "password", "Test1234!",
                        "passwordCheck", "Other1234!"))
                .when()
                .post("/users/attendee")
                .then()
                .statusCode(400)
                .body("error", containsString("Passwords do not match"));
    }

    @Test
    void createOrganizerStartsAsPending() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "organizerName", "New Company ApS",
                        "input", newUser("company@mail.dk")))
                .when()
                .post("/users/organizer")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("userRole", equalTo("ORGANIZER"))
                .body("status", equalTo("PENDING"));
    }

    @Test
    void createAdminAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(newUser("newadmin@mail.dk"))
                .when()
                .post("/users/admin")
                .then()
                .statusCode(201)
                .body("userRole", equalTo("ADMIN"));
    }

    @Test
    void createAdminWithoutTokenGives401() {
        given()
                .contentType(ContentType.JSON)
                .body(newUser("newadmin@mail.dk"))
                .when()
                .post("/users/admin")
                .then()
                .statusCode(401);
    }

    @Test
    void createAdminAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(newUser("newadmin@mail.dk"))
                .when()
                .post("/users/admin")
                .then()
                .statusCode(403);
    }

    @Test
    void readMyUser() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my")
                .then()
                .statusCode(200)
                .body("email", equalTo("bo@mail.dk"))
                .body("name", equalTo("Bo"));
    }

    @Test
    void readMyUserWithoutTokenGives401() {
        given()
                .when()
                .get("/users/my")
                .then()
                .statusCode(401);
    }

    @Test
    void updateMyUser() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Bo Updated", "email", "bo@mail.dk", "phone", "11223344"))
                .when()
                .put("/users/my")
                .then()
                .statusCode(200)
                .body("name", equalTo("Bo Updated"))
                .body("phone", equalTo("11223344"));
    }

    @Test
    void updateMyUserToTakenEmailGives409() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Bo", "email", "freja@mail.dk", "phone", "12345678"))
                .when()
                .put("/users/my")
                .then()
                .statusCode(409);
    }

    @Test
    void readAllUsersAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", is(15));
    }

    @Test
    void readAllUsersAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users")
                .then()
                .statusCode(403);
    }

    @Test
    void readUserByIdAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/" + idOf("attendee2"))
                .then()
                .statusCode(200)
                .body("email", equalTo("freja@mail.dk"));
    }

    @Test
    void readUserByIdNotFoundGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/999")
                .then()
                .statusCode(404);
    }

    @Test
    void readUserByIdNegativeIdGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/-5")
                .then()
                .statusCode(400)
                .body("error", equalTo("Id must be positive"));
    }

    @Test
    void updateUserByIdAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Freja Updated", "email", "freja@mail.dk", "phone", "23456789"))
                .when()
                .put("/users/" + idOf("attendee2"))
                .then()
                .statusCode(200)
                .body("name", equalTo("Freja Updated"));
    }

    @Test
    void deleteUserAsAdmin() {
        int id = idOf("attendee3");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/users/" + id)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void deleteUserAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .delete("/users/" + idOf("attendee3"))
                .then()
                .statusCode(403);
    }

    @Test
    void readPendingOrganizersByDefault() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/organizers")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("status", everyItem(equalTo("PENDING")));
    }

    @Test
    void readActiveOrganizers() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/organizers?status=ACTIVE")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("status", everyItem(equalTo("ACTIVE")));
    }

    @Test
    void readOrganizersWithUnknownStatusGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/users/organizers?status=xyz")
                .then()
                .statusCode(400);
    }

    @Test
    void readOrganizersAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/organizers")
                .then()
                .statusCode(403);
    }

    @Test
    void approveOrganizerLetsThemLogIn() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "ACTIVE"))
                .when()
                .put("/users/" + idOf("organizer") + "/status")
                .then()
                .statusCode(200)
                .body("status", equalTo("ACTIVE"));

        login("nike@mail.dk", "12345678");
    }

    @Test
    void changeStatusOfNonOrganizerGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", "ACTIVE"))
                .when()
                .put("/users/" + idOf("attendee") + "/status")
                .then()
                .statusCode(404);
    }

    @Test
    void changeStatusWithoutStatusGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .put("/users/" + idOf("organizer") + "/status")
                .then()
                .statusCode(400);
    }

    @Test
    void addAndRemoveFavourite() {
        int eventId = firstEventId();

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .post("/users/my/favorites/" + eventId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/favorites")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", equalTo(eventId));

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .delete("/users/my/favorites/" + eventId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/favorites")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void addingSameFavouriteTwiceKeepsOne() {
        int eventId = firstEventId();

        for (int i = 0; i < 2; i++) {
            given()
                    .header("Authorization", "Bearer " + attendeeToken)
                    .when()
                    .post("/users/my/favorites/" + eventId)
                    .then()
                    .statusCode(204);
        }

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/favorites")
                .then()
                .body("size()", is(1));
    }

    @Test
    void favouriteDoesNotAddToLiked() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .post("/users/my/favorites/" + firstEventId())
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/liked")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void addAndRemoveLiked() {
        int eventId = firstEventId();

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .post("/users/my/liked/" + eventId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/liked")
                .then()
                .statusCode(200)
                .body("size()", is(1));

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .delete("/users/my/liked/" + eventId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/users/my/liked")
                .then()
                .body("size()", is(0));
    }

    @Test
    void favouriteUnknownEventGives404() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .post("/users/my/favorites/999")
                .then()
                .statusCode(404);
    }

    @Test
    void favouritesAsOrganizerGives403() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .get("/users/my/favorites")
                .then()
                .statusCode(403);
    }

    private int idOf(String key) {
        return userMap.get(key).getId();
    }

    private int firstEventId() {
        return eventMap.get("event").getId();
    }

    private Map<String, String> newUser(String email) {
        return Map.of(
                "name", "Test Person",
                "email", email,
                "phone", "87654321",
                "password", "Test1234!",
                "passwordCheck", "Test1234!"
        );
    }

    private void resetData() {
        TestDataCreator.clearDatabase(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);
    }
}