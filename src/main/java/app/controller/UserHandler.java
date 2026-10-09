package app.controller;

import app.dto.user.OrganizerDTOResponse;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.service.UserService;
import app.utils.UserValidator;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHandler implements IHandler {
    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void create(Context ctx) {
        UserDTORequest userDTORequest = userInputValidator(ctx);
        UserDTOResponse userDTOResponse = userService.create(userDTORequest);
        if (userDTOResponse != null) {
            ctx.status(HttpStatus.CREATED);
            ctx.json(userDTOResponse);
        } else  {
            ctx.status(HttpStatus.BAD_REQUEST);
            log.warn("400: Invalid user request: {}", userDTORequest);
        }
    }

    public void createPendingOrganizer(Context ctx) {
        UserDTORequest userDTORequest = userInputValidator(ctx);
        OrganizerDTOResponse organizerDTOResponse = userService.createPendingOrganizer(userDTORequest);
        if (organizerDTOResponse != null) {
            ctx.status(HttpStatus.CREATED);
        } else  {
            ctx.status(HttpStatus.BAD_REQUEST);
            log.warn("400: Invalid organizer request: {}", userDTORequest);
        }
    }

    public void createAdmin(Context ctx) {
        UserDTORequest userDTORequest = userInputValidator(ctx);
        UserDTOResponse userDTOResponse = userService.createAdmin(userDTORequest);
        if (userDTOResponse != null) {
            ctx.status(HttpStatus.CREATED);
            ctx.json(userDTOResponse);
        } else   {
            ctx.status(HttpStatus.BAD_REQUEST);
            log.warn("400: Invalid admin request: {}", userDTORequest);
        }
    }

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

    }

    @Override
    public void getAll(Context ctx) {

    }

    @Override
    public void updateById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

    }

    @Override
    public void deleteById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

    }

    private UserDTORequest userInputValidator(Context ctx) {
        return ctx.bodyValidator(UserDTORequest.class)
                .check(user -> user.email() != null && !user.email().isBlank(), "Please enter an email")
                .check(user -> user.phone() != null && !user.phone().isEmpty(),"Please enter a phone number")
                .check(user -> user.name() != null && !user.name().isEmpty(),"Please enter your name")
                .check(user -> UserValidator.passwordsMustMatch(user.password(), user.passwordCheck()),"Passwords do not match")
                .check(user -> UserValidator.passwordMustContainNumber(user.password()),"Password must contain a number")
                .check(user -> UserValidator.shouldRejectPasswordWithoutSpecialCharacter(user.password()),"Password must contain a special character")
                .check(user -> UserValidator.isPasswordLongEnough(user.password()),"Password must be at least 8 characters long")
                .check(user -> UserValidator.validateEmail(user.email()), "Email-format not right")
                .check(user -> UserValidator.validatePhoneNumber(user.phone()), "Please enter a valid phone number")
                .get();
    }
}
