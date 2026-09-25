package app.controller;

import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;

@AllArgsConstructor
public class EventController implements EndpointGroup {
    private final EventHandler eventHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/events", eventHandler::createEvent);
        get("/api/v1/events/{id}", eventHandler::getEventById);
        get("/api/v1/events", eventHandler::getAllEvents);
        put("/api/v1/events/{id}", eventHandler::updateEventById);
        delete("/api/v1/events/{id}", eventHandler::deleteEventById);
    }
}
