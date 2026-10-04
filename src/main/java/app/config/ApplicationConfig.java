package app.config;

import app.controller.*;
import app.dao.*;
import app.service.AddressService;
import app.service.AdvertService;
import app.service.EventService;
import app.service.UserService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;

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
            ctx.status(400).json(e.getErrors());
        });
//        app.exception(Exception.class, (e, ctx) -> { // TODO: First check if validating of LocalDate fail with eg. "tomorrow" String instead of 2026-05-05 else use this to catch it
//            ctx.status(400).json(Map.of("ERROR", List.of("Invalid request body: " + e.getMessage())));
//        });
        app.start(port);
        return app;
    }

    public void stopServer(Javalin app) {
        app.stop();
    }
}
