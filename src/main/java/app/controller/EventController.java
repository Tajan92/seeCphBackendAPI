package app.controller;

import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;

@AllArgsConstructor
public class EventController implements EndpointGroup {
    private final EventHandler eventHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/events", eventHandler::create);
        get("/api/v1/events", eventHandler::getAll);
        get("/api/v1/events/active-categories", eventHandler::getAllActiveEventCategories);
        get("/api/v1/events/{id}", eventHandler::getById);
        put("/api/v1/events/{id}", eventHandler::updateById);
        delete("/api/v1/events/{id}", eventHandler::deleteById);
        post("/api/v1/events/ticketmaster", eventHandler::syncEventsFromAPI);
    }
}
