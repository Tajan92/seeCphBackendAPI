package app.controller;
import app.enums.EventCategory;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

@AllArgsConstructor
public class AdvertController implements EndpointGroup {
    private final AdvertHandler advertHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/adverts", advertHandler::create, UserRole.ORGANIZER, UserRole.ADMIN);
        get("/api/v1/adverts/{id}", advertHandler::getById, UserRole.ADMIN);
        get("/api/v1/adverts", advertHandler::getAll, UserRole.ADMIN);
        put("/api/v1/adverts/{id}", advertHandler::updateById, UserRole.ADMIN);
        delete("/api/v1/adverts/{id}", advertHandler::deleteById, UserRole.ADMIN);
    }
}
