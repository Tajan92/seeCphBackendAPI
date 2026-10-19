package app.controller;

import app.dto.user.*;
import app.enums.Status;
import app.service.UserService;
import app.utils.PathUtil;
import app.utils.UserValidator;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class UserHandler implements IHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void create(Context ctx) {
        UserDTORequest input = createValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(userService.create(input));
    }

    public void createPendingOrganizer(Context ctx) {
        OrganizerDTORequest input = ctx.bodyValidator(OrganizerDTORequest.class)
                .check(u -> u.input().name() != null && !u.input().name().isBlank(), "Please enter your name")
                .check(u -> UserValidator.validateEmail(u.input().email()), "Please enter a valid email")
                .check(u -> UserValidator.validatePhoneNumber(u.input().phone()), "Please enter a valid phone number")
                .check(u -> UserValidator.passwordsMustMatch(u.input().password(), u.input().passwordCheck()), "Passwords do not match")
                .check(u -> UserValidator.isPasswordLongEnough(u.input().password()), "Password must be at least 8 characters long")
                .check(u -> UserValidator.passwordMustContainNumber(u.input().password()), "Password must contain a number")
                .check(u -> UserValidator.shouldRejectPasswordWithoutSpecialCharacter(u.input().password()), "Password must contain a special character")
                .check(u -> u.organizerName() != null && !u.organizerName().isBlank(), "Please enter company or organisation name")
                .get();

        ctx.status(HttpStatus.CREATED).json(userService.createPendingOrganizer(input));
    }

    public void createAdmin(Context ctx) {
        UserDTORequest input = createValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(userService.createAdmin(input));
    }

    public void getCurrentUser(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(userService.getById(currentUser.id()));
    }

    @Override
    public void getById(Context ctx) {
        int id = PathUtil.getId(ctx);
        ctx.status(HttpStatus.OK).json(userService.getById(id));
    }

    @Override
    public void getAll(Context ctx) {
        ctx.status(HttpStatus.OK).json(userService.getAll());
    }

    @Override
    public void updateById(Context ctx) {
        int id = PathUtil.getId(ctx);
        UserDTORequest input = updateValidator(ctx);
        ctx.status(HttpStatus.OK).json(userService.updateById(id, input));
    }

    public void updateCurrentUser(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        UserDTORequest input = updateValidator(ctx);
        ctx.status(HttpStatus.OK).json(userService.updateById(currentUser.id(), input));
    }

    @Override
    public void deleteById(Context ctx) {
        int id = PathUtil.getId(ctx);
        userService.deleteById(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void addLikedEvent(Context ctx) {
        int eventId = PathUtil.getId(ctx);
        UserDTOResponse currentUser = ctx.attribute("user");
        userService.addLikedEvent(currentUser.id(), eventId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void getLikedEvents(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(userService.getLikedEvents(currentUser.id()));
    }

    public void removeLikedEvent(Context ctx) {
        int eventId = PathUtil.getId(ctx);
        UserDTOResponse currentUser = ctx.attribute("user");
        userService.removeLikedEvent(currentUser.id(), eventId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void addFavoriteEvent(Context ctx) {
        int eventId = PathUtil.getId(ctx);
        UserDTOResponse currentUser = ctx.attribute("user");
        userService.addFavoriteEvent(currentUser.id(), eventId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void getFavoriteEvents(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(userService.getFavoriteEvents(currentUser.id()));
    }

    public void removeFavoriteEvent(Context ctx) {
        int eventId = PathUtil.getId(ctx);
        UserDTOResponse currentUser = ctx.attribute("user");
        userService.removeFavoriteEvent(currentUser.id(), eventId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void getAllOrganizersByStatus(Context ctx) {
        Status status = ctx.queryParamAsClass("status", Status.class).getOrDefault(Status.PENDING);
        ctx.status(HttpStatus.OK).json(userService.getAllOrganizersByStatus(status));
    }

    public void updateOrganizerStatus(Context ctx) {
        int id = PathUtil.getId(ctx);
        OrganizerStatusDTORequest organizerStatusDTORequest = ctx.bodyValidator(OrganizerStatusDTORequest.class)
                .check(organizer -> organizer.status() != null, "Status is required").get();
        ctx.status(HttpStatus.OK).json(userService.updateOrganizerStatus(id, organizerStatusDTORequest.status()));
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
}