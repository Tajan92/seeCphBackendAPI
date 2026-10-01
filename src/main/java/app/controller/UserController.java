package app.controller;

import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

@AllArgsConstructor
public class UserController implements EndpointGroup {
    private final UserHandler userHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/users", userHandler::create);
        get("/api/v1/users/{id}", userHandler::getById);
        get("/api/v1/users", userHandler::getAll);
        put("/api/v1/users/{id}", userHandler::updateById);
        delete("/api/v1/users/{id}", userHandler::deleteById);
    }
}
