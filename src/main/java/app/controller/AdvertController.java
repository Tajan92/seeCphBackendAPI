package app.controller;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

@AllArgsConstructor
public class AdvertController implements EndpointGroup {
    private final AdvertHandler advertHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/adverts", advertHandler::create);
        get("/api/v1/adverts/{id}", advertHandler::getById);
        get("/api/v1/adverts", advertHandler::getAll);
        put("/api/v1/adverts/{id}", advertHandler::updateById);
        delete("/api/v1/adverts/{id}", advertHandler::deleteById);
    }
}
