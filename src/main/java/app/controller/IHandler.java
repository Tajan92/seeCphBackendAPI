package app.controller;

import io.javalin.http.Context;

public interface IHandler {
    public void create(Context ctx);
    public void getById(Context ctx);
    public void getAll(Context ctx);
    public void updateById(Context ctx);
    public void deleteById(Context ctx);
}
