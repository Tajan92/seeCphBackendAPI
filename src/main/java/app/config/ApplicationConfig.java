package app.config;

import app.controller.*;
import app.dao.*;
import app.service.AdvertService;
import app.service.EventService;
import app.service.UserService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

public class ApplicationConfig implements EndpointGroup {

    private final EventController eventController;
    private final UserController userController;
    private final AdvertController advertController;

    public ApplicationConfig(EntityManagerFactory emf) {
        // DAOs
        AddressDAO addressDAO = new AddressDAO(emf);
        AdvertDAO advertDAO = new AdvertDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Services
        EventService eventService = new EventService(eventDAO);
        UserService userService = new UserService(userDAO);
        AdvertService advertService = new AdvertService(advertDAO);

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
}
