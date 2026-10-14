package app.utils;

import io.javalin.http.Context;

public class PathUtil {
    public static int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class)
                .check(value -> value > 0, "Id must be positive")
                .get();
    }
}
