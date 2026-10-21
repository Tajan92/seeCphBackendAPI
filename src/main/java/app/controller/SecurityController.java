package app.controller;

import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.*;

@AllArgsConstructor
public class SecurityController implements EndpointGroup {
    private final SecurityHandler securityHandler;

    @Override
    public void addEndpoints() {
        post("/api/v1/login", securityHandler::login, UserRole.ANYONE);
        put("/api/v1/users/my/password", securityHandler::changePassword, UserRole.ADMIN, UserRole.ATTENDEE, UserRole.ORGANIZER);
        get("/api/v1/auth/healthcheck", securityHandler::healthCheck, UserRole.ANYONE);
    }
}
