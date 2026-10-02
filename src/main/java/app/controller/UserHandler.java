package app.controller;

import app.service.UserService;
import io.javalin.http.Context;

public class UserHandler implements IHandler{
    UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void create(Context ctx) {

    }

    public void createPendingOrganizer(Context ctx) {

    }

    public void createAdmin(Context ctx) {

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
