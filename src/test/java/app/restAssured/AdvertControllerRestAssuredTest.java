package app.restAssured;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
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
    private static Map<String, Advert> advertMap;
    private static Map<String, Event> eventMap;
    private static Map<String, User> userMap;
    private static EventDAO eventDAO;
    private static UserDAO userDAO;


    @BeforeAll
    static void init() {
        emf = HibernateConfig.getEntityManagerFactory();
        applicationConfig = new ApplicationConfig(emf);
        app = applicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api/v1/adverts";

    }

    @BeforeEach
    void setUp() {
        TestDataCreator.clearDatabase(emf);
        advertMap = TestDataCreator.createAdverts(emf);
        eventMap = TestDataCreator.createEvents(emf);
        userMap = TestDataCreator.createUsers(emf);
        eventDAO = new EventDAO(emf);
        userDAO = new UserDAO(emf);
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

    @Test
    void createAdvert() {
        AdvertDTORequest advertDTORequest = new AdvertDTORequest(eventDAO.readAll().getFirst().getId(), userDAO.readAll().getFirst().getUserId(), AddPlacement.FRONTPAGE_HIGHLIGHT,LocalDate.of(2027,1,5), LocalDate.of(2027,1,6));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(advertDTORequest)
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
                .getList("", AdvertDTOResponse.class);
    }

    @Test
    void createAdvertFailure() {
        AdvertDTORequest advertDTORequest = new AdvertDTORequest(eventDAO.readAll().getFirst().getId(), userDAO.readAll().getFirst().getUserId(), AddPlacement.FRONTPAGE_HIGHLIGHT,LocalDate.of(2027,1,5), LocalDate.of(2027,1,4));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(advertDTORequest)
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
                .getList("", AdvertDTOResponse.class);
    }

    @Test
    void readAdvertById() {
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
    void readAdvertByIdFailure1() {
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
    void readAdvertByIdFailure2() {
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
    void updateAdvert() {
        AdvertDTORequest advertDTORequest = new AdvertDTORequest(eventDAO.readAll().getFirst().getId(), userDAO.readAll().getFirst().getUserId(), AddPlacement.FRONTPAGE_HIGHLIGHT,LocalDate.of(2027,1,5), LocalDate.of(2027,1,6));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(advertDTORequest)
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
                .getList("", AdvertDTOResponse.class);
    }

    @Test
    void updateAdvertFailure1() {
        AdvertDTORequest advertDTORequest = new AdvertDTORequest(eventDAO.readAll().getFirst().getId(), userDAO.readAll().getFirst().getUserId(), AddPlacement.FRONTPAGE_HIGHLIGHT,LocalDate.of(2027,1,5), LocalDate.of(2027,1,6));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(advertDTORequest)
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
                .getList("", AdvertDTOResponse.class);
    }

    @Test
    void updateAdvertFailure2() {
        AdvertDTORequest advertDTORequest = new AdvertDTORequest(eventDAO.readAll().getFirst().getId(), userDAO.readAll().getFirst().getUserId(), AddPlacement.FRONTPAGE_HIGHLIGHT,LocalDate.of(2027,1,5), LocalDate.of(2027,1,6));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(advertDTORequest)
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
                .getList("", AdvertDTOResponse.class);
    }

    @Test
    void deleteAdvert() {
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
                .getList("", AdvertDTOResponse.class);
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
                .getList("", AdvertDTOResponse.class);
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
                .getList("", AdvertDTOResponse.class);
    }
}
