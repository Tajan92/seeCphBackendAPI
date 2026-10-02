package app.controller;

import app.service.IService;

import javax.naming.Context;
import java.util.function.Function;

public abstract class GenericHandler<T, R> {
    private final IService<T, R> service;

    public GenericHandler(IService<T, R> service) {
        this.service = service;
    }

    public void create(Context ctx, Function<Context, T> inputValidator) {
        T input = inputValidator.apply(ctx);
        service.create(input);

    }
}
