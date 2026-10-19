package app.restAssured;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.dto.event.EventDTORequest;
import app.entities.Event;
import app.entities.users.User;
import app.enums.EventCategory;
import app.utils.TestDataCreator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerRestAssuredTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static ApplicationConfig applicationConfig;

    private Map<String, Event> eventMap;
    private Map<String, User> userMap;

    private String adminToken;
    private String organizerToken;
    private String attendeeToken;

    @BeforeAll
    static void init() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        applicationConfig = new ApplicationConfig(emf);
        app = applicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api/v1";
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);

        adminToken = login(userMap.get("admin").getEmail(), "12345678");
        organizerToken = login(userMap.get("organizer2").getEmail(), "23456789");
        attendeeToken = login(userMap.get("attendee").getEmail(), "12345678");
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

    private EventDTORequest validEvent(String title) {
        return new EventDTORequest(title, "Description", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1",
                LocalTime.of(18, 0), LocalDate.of(2027, 6, 6), "https://www.newevent.dk", "https://www.image.dk",
                false, EventCategory.MUSIC);
    }

    private int createEvent(String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(validEvent("My Event"))
                .when()
                .post("/events")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private void assertEventCount(int expected) {
        given()
                .when()
                .get("/events")
                .then()
                .statusCode(200)
                .body("size()", is(expected));
    }

    @Test
    void readAllEventsIsPublic() {
        given()
                .when()
                .get("/events")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void readEventsFilteredByCategory() {
        given()
                .when()
                .get("/events?category=Comedy")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].title", equalTo("Stand-up Comedy Night"));
    }

    @Test
    void readEventsWithUnknownCategoryGives400() {
        given()
                .when()
                .get("/events?category=NotACategory")
                .then()
                .statusCode(400);
    }

    @Test
    void readEventsWithWrongDateFormatGives400() {
        given()
                .when()
                .get("/events?startDate=15-11-2026")
                .then()
                .statusCode(400);
    }

    @Test
    void readActiveCategories() {
        given()
                .when()
                .get("/events/active-categories")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void readEventById() {
        given()
                .when()
                .get("/events/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Lukas Graham"));
    }

    @Test
    void readEventByIdNotFoundGives404() {
        given()
                .when()
                .get("/events/10")
                .then()
                .statusCode(404);
    }

    @Test
    void readEventByIdNegativeIdGives400() {
        given()
                .when()
                .get("/events/-5")
                .then()
                .statusCode(400)
                .body("error", equalTo("Id must be positive"));
    }

    @Test
    void createEventAsOrganizer() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(validEvent("New Event"))
                .when()
                .post("/events")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("title", equalTo("New Event"))
                .body("id", notNullValue());

        assertEventCount(3);
    }

    @Test
    void createEventWithBlankDescriptionGives400() {
        EventDTORequest request = new EventDTORequest("New Event", "", 200, false, "Kongens Lyngby", "2800",
                "Hovedgaden 1", LocalTime.of(18, 0), LocalDate.of(2027, 6, 6), "https://www.newevent.dk",
                "https://www.image.dk", false, EventCategory.MUSIC);

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/events")
                .then()
                .statusCode(400)
                .body("error", containsString("Description must be filled out"));

        assertEventCount(2);
    }

    @Test
    void createEventWithoutTokenGives401() {
        given()
                .contentType(ContentType.JSON)
                .body(validEvent("No token"))
                .when()
                .post("/events")
                .then()
                .statusCode(401);

        assertEventCount(2);
    }

    @Test
    void createEventAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(validEvent("Attendee event"))
                .when()
                .post("/events")
                .then()
                .statusCode(403);

        assertEventCount(2);
    }

    @Test
    void readMyEventsAsOrganizer() {
        int myEventId = createEvent(organizerToken);

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .get("/events/mine")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", equalTo(myEventId));
    }

    @Test
    void readMyEventsAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .get("/events/mine")
                .then()
                .statusCode(403);
    }

    @Test
    void updateEventAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(validEvent("Updated by admin"))
                .when()
                .put("/events/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("title", equalTo("Updated by admin"));

        assertEventCount(2);
    }

    @Test
    void updateOwnEventAsOrganizer() {
        int myEventId = createEvent(organizerToken);

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(validEvent("My Updated Event"))
                .when()
                .put("/events/" + myEventId)
                .then()
                .statusCode(200)
                .body("title", equalTo("My Updated Event"));
    }

    @Test
    void updateOthersEventAsOrganizerGives403() {
        // Event 1 is not owned by organizer2
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(validEvent("Hijacked"))
                .when()
                .put("/events/1")
                .then()
                .statusCode(403)
                .body("error", equalTo("You can only change your own events"));
    }

    @Test
    void updateEventAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(validEvent("Attendee edit"))
                .when()
                .put("/events/1")
                .then()
                .statusCode(403);
    }

    @Test
    void updateEventNegativeIdGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(validEvent("New Event"))
                .when()
                .put("/events/-5")
                .then()
                .statusCode(400);

        assertEventCount(2);
    }

    @Test
    void updateEventNotFoundGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(validEvent("New Event"))
                .when()
                .put("/events/10")
                .then()
                .statusCode(404);

        assertEventCount(2);
    }

    @Test
    void deleteEventAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/events/1")
                .then()
                .statusCode(204);

        assertEventCount(1);
    }

    @Test
    void deleteOwnEventAsOrganizer() {
        int myEventId = createEvent(organizerToken);

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .delete("/events/" + myEventId)
                .then()
                .statusCode(204);

        assertEventCount(2);
    }

    @Test
    void deleteOthersEventAsOrganizerGives403() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .delete("/events/1")
                .then()
                .statusCode(403);

        assertEventCount(2);
    }

    @Test
    void deleteFavouriteEventRemovesItFromFavourites() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .post("/users/my/favorites/1")
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/events/1")
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
    void deleteEventNegativeIdGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/events/-5")
                .then()
                .statusCode(400);

        assertEventCount(2);
    }

    @Test
    void deleteEventNotFoundGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/events/10")
                .then()
                .statusCode(404);

        assertEventCount(2);
    }
}
