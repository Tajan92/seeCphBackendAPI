package app.controller;

import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;

@AllArgsConstructor
public class EventController implements EndpointGroup {
    private final EventHandler eventHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/events", eventHandler::create, UserRole.ORGANIZER, UserRole.ADMIN);
        get("/api/v1/events", eventHandler::getAll, UserRole.ADMIN);
        get("/api/v1/events/active-categories", eventHandler::getAllActiveEventCategories, UserRole.ANYONE);
        get("/api/v1/events/{id}", eventHandler::getById, UserRole.ADMIN);
        put("/api/v1/events/{id}", eventHandler::updateById, UserRole.ADMIN);
        delete("/api/v1/events/{id}", eventHandler::deleteById, UserRole.ADMIN);
        post("/api/v1/events/ticketmaster", eventHandler::syncEventsFromAPI, UserRole.ANYONE);
    }
}
