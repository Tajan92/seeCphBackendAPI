package app.controller;

import app.dto.user.OrganizerDTORequest;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.service.UserService;
import app.utils.UserValidator;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class UserHandler implements IHandler {
    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void create(Context ctx) {
        UserDTORequest input = createValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(userService.create(input));
    }

    public void createPendingOrganizer(Context ctx) {
        UserDTORequest input = createValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(userService.createPendingOrganizer(input));
    }

    public void createAdmin(Context ctx) {
        UserDTORequest input = createValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(userService.createAdmin(input));
    }

    public void getMe(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(userService.getById(currentUser.id()));
    }

    @Override
    public void getById(Context ctx) {
        int id = getId(ctx);
        ctx.status(HttpStatus.OK).json(userService.getById(id));
    }

    @Override
    public void getAll(Context ctx) {
        ctx.status(HttpStatus.OK).json(userService.getAll());
    }

    @Override
    public void updateById(Context ctx) {
        int id = getId(ctx);
        UserDTORequest input = updateValidator(ctx);
        ctx.status(HttpStatus.OK).json(userService.updateById(id, input));
    }

    public void updateMe(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        UserDTORequest input = updateValidator(ctx);
        ctx.status(HttpStatus.OK).json(userService.updateById(currentUser.id(), input));
    }

    public void updateOrganizerStatus(Context ctx) {
        int id = getId(ctx);
        OrganizerDTORequest input = ctx.bodyValidator(OrganizerDTORequest.class)
                .check(r -> r.status() != null, "Status is required")
                .get();
        ctx.status(HttpStatus.OK).json(userService.updateOrganizerStatus(id, input.status()));
    }

    @Override
    public void deleteById(Context ctx) {
        int id = getId(ctx);
        userService.deleteById(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    private UserDTORequest createValidator(Context ctx) {
        return ctx.bodyValidator(UserDTORequest.class)
                .check(u -> u.name() != null && !u.name().isBlank(), "Please enter your name")
                .check(u -> UserValidator.validateEmail(u.email()), "Please enter a valid email")
                .check(u -> UserValidator.validatePhoneNumber(u.phone()), "Please enter a valid phone number")
                .check(u -> UserValidator.passwordsMustMatch(u.password(), u.passwordCheck()), "Passwords do not match")
                .check(u -> UserValidator.isPasswordLongEnough(u.password()), "Password must be at least 8 characters long")
                .check(u -> UserValidator.passwordMustContainNumber(u.password()), "Password must contain a number")
                .check(u -> UserValidator.shouldRejectPasswordWithoutSpecialCharacter(u.password()), "Password must contain a special character")
                .get();
    }

    private UserDTORequest updateValidator(Context ctx) {
        return ctx.bodyValidator(UserDTORequest.class)
                .check(u -> u.name() != null && !u.name().isBlank(), "Please enter your name")
                .check(u -> UserValidator.validateEmail(u.email()), "Please enter a valid email")
                .check(u -> UserValidator.validatePhoneNumber(u.phone()), "Please enter a valid phone number")
                .get();
    }

    private int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
    }
}
