package app.config;

import app.controller.*;
import app.dao.*;
import app.enums.AddPlacement;
import app.enums.Status;
import app.exceptions.*;
import app.service.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationConfig implements EndpointGroup {

    private final EventController eventController;
    private final UserController userController;
    private final AdvertController advertController;
    private final SecurityController securityController;
    private final SecurityHandler securityHandler;

    public ApplicationConfig(EntityManagerFactory emf) {
        // DAOs
        AdvertDAO advertDAO = new AdvertDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        AddressDAO addressDAO = new AddressDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Services
        AddressService addressService = new AddressService(addressDAO);
        UserService userService = new UserService(userDAO);
        EventService eventService = new EventService(eventDAO, addressService, userDAO);
        AdvertService advertService = new AdvertService(advertDAO, userDAO, eventDAO);
        SecurityService securityService = new SecurityService(userDAO);

        // Handlers
        EventHandler eventHandler = new EventHandler(eventService);
        UserHandler userHandler = new UserHandler(userService);
        AdvertHandler advertHandler = new AdvertHandler(advertService);
        this.securityHandler = new SecurityHandler(securityService);

        // Controllers
        this.eventController = new EventController(eventHandler);
        this.userController = new UserController(userHandler);
        this.advertController = new AdvertController(advertHandler);
        this.securityController = new SecurityController(securityHandler);
    }

    @Override
    public void addEndpoints() {
        securityController.addEndpoints();
        eventController.addEndpoints();
        userController.addEndpoints();
        advertController.addEndpoints();
    }

    public void configuration(JavalinConfig config) {
        config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }));
        config.showJavalinBanner = false;
        config.http.defaultContentType = "application/json";
        config.router.apiBuilder(this);
        config.bundledPlugins.enableCors(cors -> {
            cors.addRule(rule -> {
                rule.allowHost("http://localhost:5173", "https://www.seecph.theisrud.dk");
            });
        });
        config.staticFiles.add(staticFiles -> {
            staticFiles.hostedPath = "/images";
            staticFiles.directory = "/images";
            staticFiles.location = Location.CLASSPATH;
        });
        config.validation.register(Status.class, value -> Status.valueOf(value.trim().toUpperCase()));
        config.validation.register(AddPlacement.class, value -> AddPlacement.valueOf(value.trim().toUpperCase()));
    }

    public Javalin startServer(int port) {
        var app = Javalin.create(this::configuration);
        app.beforeMatched(securityHandler::authenticate);
        app.beforeMatched(securityHandler::authorize);
        exceptionHandling(app);
        app.start(port);
        return app;
    }

    public void stopServer(Javalin app) {
        app.stop();
    }

    private static void exceptionHandling(Javalin app) {
        app.exception(ValidationException.class, (e, ctx) -> {
            String messages = e.getErrors().values().stream()
                    .flatMap(List::stream)
                    .map(ValidationError::getMessage)
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed: {}", messages);
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", messages));
        });
        app.exception(ApiException.class, (e, ctx) -> {
            log.error("External API error ({}): {}", e.getCode(), e.getMessage(), e);
            ctx.status(e.getCode()).json(Map.of("error", "An error occurred while fetching data from an external service"));
        });

        app.exception(SecurityValidationException.class, (e, ctx) -> {
            log.error("Security Validation Error: {}", e.getMessage(), e);
            ctx.status(e.getCode()).json(Map.of("error", e.getMessage()));
        });

        app.exception(DatabaseException.class, (e, ctx) -> {
            log.warn("Database Error: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "An unexpected internal server error occurred"));
        });

        app.exception(DatabaseIdException.class, (e, ctx) -> {
            log.warn("Database Id Error: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", e.getMessage()));
        });

        app.exception(ConflictException.class, (e, ctx) -> {
            log.warn("Conflict: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).json(Map.of("error", e.getMessage()));
        });

        app.exception(BadRequestException.class, (e, ctx) -> {
            log.warn("Bad request: {}", e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", e.getMessage()));
        });

        app.exception(SyncException.class, (e, ctx) -> {
            log.error("Sync failed", e);
            ctx.status(e.getCode());
        });

        app.exception(Exception.class, (e, ctx) -> {
            log.error("Unexpected server error: {}", e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "An unexpected internal server error occurred"));
        });
    }
}
