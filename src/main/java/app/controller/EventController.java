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
        post("/api/v1/events", eventHandler::create, UserRole.ADMIN  , UserRole.ATTENDEE); // TODO: Figure out role to set and how it works??
        get("/api/v1/events/{id}", eventHandler::getById);
        get("/api/v1/events", eventHandler::getAll);
        put("/api/v1/events/{id}", eventHandler::updateById);
        delete("/api/v1/events/{id}", eventHandler::deleteById);
    }
}
