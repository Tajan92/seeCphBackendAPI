package app.controller;

import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

@AllArgsConstructor
public class UserController implements EndpointGroup {
    private final UserHandler userHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/users/attendee", userHandler::create, UserRole.ANYONE);
        post("/api/v1/users/organizer", userHandler::createPendingOrganizer, UserRole.ANYONE);
        post("/api/v1/users/admin", userHandler::createAdmin, UserRole.ADMIN);
        get("/api/v1/users/{id}", userHandler::getById, UserRole.ADMIN);
        get("/api/v1/users", userHandler::getAll, UserRole.ADMIN);
        put("/api/v1/users/{id}", userHandler::updateById, UserRole.ADMIN);
        delete("/api/v1/users/{id}", userHandler::deleteById, UserRole.ADMIN);
    }
}
