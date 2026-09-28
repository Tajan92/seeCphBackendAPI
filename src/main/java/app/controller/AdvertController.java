package app.controller;

import io.javalin.apibuilder.EndpointGroup;

public class AdvertController implements EndpointGroup {
    private final AdvertHandler advertHandler;

    public AdvertController(AdvertHandler adverthandler)  {
        this.advertHandler = adverthandler;
    }

    @Override
    public void addEndpoints() {

    }
}
