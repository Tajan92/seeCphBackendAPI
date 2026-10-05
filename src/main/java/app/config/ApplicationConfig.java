package app.config;

import app.controller.*;
import app.dao.*;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import app.exceptions.SyncException;
import app.service.AddressService;
import app.service.AdvertService;
import app.service.EventService;
import app.service.UserService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
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

    public ApplicationConfig(EntityManagerFactory emf) {
        // DAOs
        AdvertDAO advertDAO = new AdvertDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        AddressDAO addressDAO = new AddressDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Services
        AddressService addressService = new AddressService(addressDAO);
        UserService userService = new UserService(userDAO);
        EventService eventService = new EventService(eventDAO, addressService, userService);
        AdvertService advertService = new AdvertService(advertDAO, userDAO, eventDAO, eventService, userService);

        // Handlers
        EventHandler eventHandler = new EventHandler(eventService);
        UserHandler userHandler = new UserHandler(userService);
        AdvertHandler advertHandler = new AdvertHandler(advertService);

        // Controllers
        this.eventController = new EventController(eventHandler);
        this.userController = new UserController(userHandler);
        this.advertController = new AdvertController(advertHandler);
    }

    @Override
    public void addEndpoints() {
        eventController.addEndpoints();
        userController.addEndpoints();
        advertController.addEndpoints();
    }

    public void configuration(JavalinConfig config) {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
            }));
            config.router.apiBuilder(this);
    }

    public Javalin startServer(int port) {
        var app = Javalin.create(this::configuration);

        app.exception(ValidationException.class, (e, ctx) -> {
            String messages = e.getErrors().values().stream()
                    .flatMap(List::stream)
                    .map(ValidationError::getMessage)
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed: {}", messages);
            ctx.status(400).json(Map.of("error", messages));
        });
        app.exception(ApiException.class, (e, ctx) -> {
            log.warn("API Error: {}", e.getMessage());
            ctx.status(500).json(Map.of("error", e.getMessage()));
        });

        app.exception(DatabaseException.class, (e, ctx) -> {
            log.warn("Database Error: {}", e.getMessage());
            ctx.status(500).json(Map.of("error", e.getMessage()));
        });

        app.exception(DatabaseIdException.class, (e, ctx) -> {
            log.warn("Database Id Error: {}", e.getMessage());
            ctx.status(500).json(Map.of("error", e.getMessage()));
        });

        app.exception(SyncException.class, (e, ctx) -> {
            log.warn("Sync Error: {}", e.getMessage());
            ctx.status(500).json(Map.of("error", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            log.error("Unexpected server error: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "An unexpected internal server error occurred"));
        });
        app.start(port);
        return app;
    }

    public void stopServer(Javalin app) {
        app.stop();
    }
}
