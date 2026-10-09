package app.controller;

import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.post;

@AllArgsConstructor
public class SecurityController implements EndpointGroup {
    private final SecurityHandler securityHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/login", securityHandler::login, UserRole.ANYONE);
    }
}
