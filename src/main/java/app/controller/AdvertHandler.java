package app.controller;

import app.service.AdvertService;
import io.javalin.http.Context;

public class AdvertHandler implements IHandler{
    AdvertService advertService;

    public AdvertHandler(AdvertService advertService) {
        this.advertService = advertService;
    }

    @Override
    public void create(Context ctx) {

    }

    @Override
    public void getById(Context ctx) {

    }

    @Override
    public void getAll(Context ctx) {

    }

    @Override
    public void updateById(Context ctx) {

    }

    @Override
    public void deleteById(Context ctx) {

    }
}
