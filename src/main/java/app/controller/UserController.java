package app.controller;

import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserController implements EndpointGroup {
    private final UserHandler userHandler;

    @Override
    public void addEndpoints() {

    }
}
