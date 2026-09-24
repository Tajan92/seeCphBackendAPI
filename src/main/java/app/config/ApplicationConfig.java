package app.config;

import app.controller.EventController;
import app.controller.EventHandler;
import app.controller.UserController;
import app.controller.UserHandler;
import app.dao.*;
import app.service.EventService;
import app.service.UserService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

public class ApplicationConfig implements EndpointGroup {

    private final EventController eventController;
    private final UserController userController;

    public ApplicationConfig(EntityManagerFactory emf) {
        // DAOs
        AddressDAO addressDAO = new AddressDAO(emf);
        AdvertDAO advertDAO = new AdvertDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        ImageUrlDAO imageUrlDAO = new ImageUrlDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Services
        EventService eventService = new EventService(eventDAO);
        UserService userService = new UserService(userDAO);

        // Handlers
        EventHandler eventHandler = new EventHandler(eventService);
        UserHandler userHandler = new UserHandler(userService);

        // Controllers
        this.eventController = new EventController(eventHandler);
        this.userController = new UserController(userHandler);
    }

    @Override
    public void addEndpoints() {
        eventController.addEndpoints();
        userController.addEndpoints();
    }
}
