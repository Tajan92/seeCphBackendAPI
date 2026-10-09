package app.restAssured;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.HibernateTestConfig;
import app.dao.UserDAO;
import app.dto.event.EventDTORequest;
import app.dto.event.EventDTOResponse;
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
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerRestAssuredTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static ApplicationConfig applicationConfig;
    private static Map<String, Event> eventMap;
    private static Map<String, User> userMap;
    private static UserDAO userDAO;


    @BeforeAll
    static void init() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        applicationConfig = new ApplicationConfig(emf);
        app = applicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api/v1/events";
    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);
        userDAO = new UserDAO(emf);
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

    @Test
    void createEvent() {
        EventDTORequest eventDTORequest = new EventDTORequest(userDAO.readAll().getFirst().getUserId(), "New Event", "Description", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1", LocalTime.of(18,0), LocalDate.of(2027,6,6),"www.newEvent.dk", "www.image.dk", false, EventCategory.MUSIC);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(eventDTORequest)
                .when()
                .post("")
                .then()
                .statusCode(201)
                .log().all();

        given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(3))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void createEventFailure() {
        EventDTORequest eventDTORequest = new EventDTORequest(userDAO.readAll().getFirst().getUserId(), "New Event", "", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1", LocalTime.of(18,0), LocalDate.of(2027,6,6),"www.newEvent.dk", "www.image.dk", false, EventCategory.MUSIC);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(eventDTORequest)
                .when()
                .post("")
                .then()
                .statusCode(400)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void readEventById() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void readEventByIdFailure1() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .when()
                .get("/10")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    void readEventByIdFailure2() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .when()
                .get("/-5")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void updateEvent() {
        EventDTORequest eventDTORequest = new EventDTORequest(userDAO.readAll().getFirst().getUserId(), "New Event", "Description", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1", LocalTime.of(18,0), LocalDate.of(2027,6,6),"www.newEvent.dk", "www.image.dk", false, EventCategory.MUSIC);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(eventDTORequest)
                .when()
                .put("/1")
                .then()
                .statusCode(200)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void updateEventFailure1() {
        EventDTORequest eventDTORequest = new EventDTORequest(userDAO.readAll().getFirst().getUserId(), "New Event", "Description", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1", LocalTime.of(18,0), LocalDate.of(2027,6,6),"www.newEvent.dk", "www.image.dk", false, EventCategory.MUSIC);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(eventDTORequest)
                .when()
                .put("/-5")
                .then()
                .statusCode(400)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void updateEventFailure2() {
        EventDTORequest eventDTORequest = new EventDTORequest(userDAO.readAll().getFirst().getUserId(), "New Event", "Description", 200, false, "Kongens Lyngby", "2800", "Hovedgaden 1", LocalTime.of(18,0), LocalDate.of(2027,6,6),"www.newEvent.dk", "www.image.dk", false, EventCategory.MUSIC);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(eventDTORequest)
                .when()
                .put("/10")
                .then()
                .statusCode(500)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void deleteEvent() {
        given()
                .when()
                .delete("/1")
                .then()
                .statusCode(200)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(1))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void deleteAdvertFailure1() {
        given()
                .when()
                .delete("/-5")
                .then()
                .statusCode(400)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }

    @Test
    void deleteAdvertFailure2() {
        given()
                .when()
                .delete("/10")
                .then()
                .statusCode(500)
                .log().all();

        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .extract()
                .jsonPath()
                .getList("", EventDTOResponse.class);
    }
}
