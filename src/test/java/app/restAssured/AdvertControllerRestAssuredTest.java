package app.restAssured;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.dao.EventDAO;
import app.dto.advert.AdvertDTORequest;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.utils.TestDataCreator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdvertControllerRestAssuredTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static ApplicationConfig applicationConfig;
    private static EventDAO eventDAO;

    private Map<String, Advert> advertMap;
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
        eventDAO = new EventDAO(emf);
        adminToken = login(userMap.get("admin").getEmail(), "12345678");
        organizerToken = login(userMap.get("organizer2").getEmail(), "12345678");
        attendeeToken = login(userMap.get("attendee").getEmail(), "12345678");
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        advertMap = TestDataCreator.createAdverts(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);

    }

    @AfterEach
    void tearDown() {
        advertMap.clear();
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

    private int firstEventId() {
        return eventDAO.readAll().getFirst().getId();
    }

    private void assertAdvertCount(int expected) {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/adverts")
                .then()
                .statusCode(200)
                .body("size()", is(expected));
    }

    @Test
    void createAdvert() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/adverts")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("addPlacement", equalTo("FRONTPAGE_HIGHLIGHT"));

        assertAdvertCount(3);
    }

    @Test
    void createAdvertEndBeforeStartGives400() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 4));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/adverts")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("error", containsString("End date must be on or after the start date"));

        assertAdvertCount(2);
    }

    @Test
    void createAdvertWithoutTokenGives401() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/adverts")
                .then()
                .statusCode(401);

        assertAdvertCount(2);
    }

    @Test
    void createAdvertAsAttendeeGives403() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/adverts")
                .then()
                .statusCode(403);

        assertAdvertCount(2);
    }

    @Test
    void createAdvertForOthersEventAsOrganizerGives403() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HEADER,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/adverts")
                .then()
                .statusCode(403)
                .body("error", equalTo("You can only advertise your own events"));

        assertAdvertCount(2);
    }

    @Test
    void readAdvertById() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/adverts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void readAdvertByIdNotFoundGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/adverts/10")
                .then()
                .statusCode(404);
    }

    @Test
    void readAdvertByIdNegativeIdGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/adverts/-5")
                .then()
                .statusCode(400)
                .body("error", equalTo("Id must be positive"));
    }

    @Test
    void readAllAdvertsAsOrganizerGives403() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .get("/adverts")
                .then()
                .statusCode(403);
    }

    @Test
    void readActiveAdvertsIsPublic() {
        given()
                .when()
                .get("/adverts/active")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].addPlacement", equalTo("FRONTPAGE_HIGHLIGHT"));
    }

    @Test
    void readActiveAdvertsByPlacement() {
        given()
                .when()
                .get("/adverts/active?placement=FRONTPAGE_HEADER")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void readMyAdvertsAsOrganizer() {
        given()
                .header("Authorization", "Bearer " + organizerToken)
                .when()
                .get("/adverts/my-adverts")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void updateAdvert() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/adverts/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("addPlacement", equalTo("FRONTPAGE_HIGHLIGHT"))
                .body("startDate", equalTo("2027-01-05"));

        assertAdvertCount(2);
    }

    @Test
    void updateAdvertNegativeIdGives400() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/adverts/-5")
                .then()
                .statusCode(400)
                .body("error", equalTo("Id must be positive"));

        assertAdvertCount(2);
    }

    @Test
    void updateAdvertNotFoundGives404() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/adverts/10")
                .then()
                .statusCode(404);

        assertAdvertCount(2);
    }

    @Test
    void updateAdvertAsOrganizerGives403() {
        AdvertDTORequest request = new AdvertDTORequest(firstEventId(), AddPlacement.FRONTPAGE_HIGHLIGHT,
                LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 6));

        given()
                .header("Authorization", "Bearer " + organizerToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/adverts/1")
                .then()
                .statusCode(403);
    }

    @Test
    void deleteAdvert() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/adverts/1")
                .then()
                .statusCode(204);

        assertAdvertCount(1);
    }

    @Test
    void deleteAdvertNegativeIdGives400() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/adverts/-5")
                .then()
                .statusCode(400);

        assertAdvertCount(2);
    }

    @Test
    void deleteAdvertNotFoundGives404() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/adverts/10")
                .then()
                .statusCode(404);

        assertAdvertCount(2);
    }

    @Test
    void deleteAdvertAsAttendeeGives403() {
        given()
                .header("Authorization", "Bearer " + attendeeToken)
                .when()
                .delete("/adverts/1")
                .then()
                .statusCode(403);

        assertAdvertCount(2);
    }

    private void resetData() {
        TestDataCreator.clearDatabase(emf);
        advertMap = TestDataCreator.createAdverts(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);
    }
}

