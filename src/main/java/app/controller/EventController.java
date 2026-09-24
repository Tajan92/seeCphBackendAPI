package app.controller;

import io.javalin.apibuilder.EndpointGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventController implements EndpointGroup {
    private final EventHandler eventHandler;

    @Override
    public void addEndpoints() {

    }
}
