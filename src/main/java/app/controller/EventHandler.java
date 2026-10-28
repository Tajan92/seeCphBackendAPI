package app.controller;

import app.dto.event.EventDTORequest;
import app.dto.user.UserDTOResponse;
import app.enums.EventCategory;
import app.exceptions.BadRequestException;
import app.exceptions.SecurityValidationException;
import app.service.EventService;
import app.utils.PathUtil;
import app.utils.Utils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Slf4j
public class EventHandler implements IHandler {
    private final EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void create(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        EventDTORequest input = eventInputValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(eventService.create(input, currentUser.id()));
    }

    @Override
    public void getById(Context ctx) {
        ctx.status(HttpStatus.OK).json(eventService.getById(PathUtil.getId(ctx)));
    }

    @Override
    public void getAll(Context ctx) {
        int maxPageSize = 100;
        int page = Math.max(ctx.queryParamAsClass("page", Integer.class).getOrDefault(0), 0);
        int pageSize = Math.clamp(ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(20), 1, maxPageSize);
        EventCategory category = parseCategory(ctx.queryParam("category"));
        LocalDate startDate = parseDate(ctx.queryParam("startDate"));

        ctx.status(HttpStatus.OK).json(eventService.getAllEventsBySearchAndFilter(
                ctx.queryParam("search"), category, startDate, ctx.queryParam("postalCode"), page, pageSize));
    }

    public void getMyEvents(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(eventService.getByUserId(currentUser.id()));
    }

    public void getAllActiveEventCategories(Context ctx) {
        ctx.status(HttpStatus.OK).json(eventService.getAllActiveEventCategories());
    }

    @Override
    public void updateById(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        int id = PathUtil.getId(ctx);
        EventDTORequest input = eventInputValidator(ctx);
        ctx.status(HttpStatus.OK).json(eventService.updateById(id, input, currentUser));
    }

    @Override
    public void deleteById(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        eventService.deleteById(PathUtil.getId(ctx), currentUser);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void syncEventsFromAPI(Context ctx) {
        String authHeader = ctx.header("x-api-key");
        String expectedSecret = Utils.getOptionalConfig("SYNC_SECRET");

        if (expectedSecret == null || !expectedSecret.equals(authHeader)) {
            throw new SecurityValidationException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized");
        }
        eventService.syncEvents();
        ctx.status(HttpStatus.ACCEPTED).result("Ticketmaster sync started successfully.");
    }

    private static EventCategory parseCategory(String categoryInput) {
        if (categoryInput == null || categoryInput.isBlank()) return null;

        EventCategory category = EventCategory.findByLabelOrName(categoryInput.trim());
        if (category == null) {
            throw new BadRequestException("Unknown category: " + categoryInput);
        }
        return category;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("startDate must be in the format yyyy-MM-dd");
        }
    }

    private EventDTORequest eventInputValidator(Context ctx) {
        return ctx.bodyValidator(EventDTORequest.class)
                .check(e -> e.title() != null && !e.title().isBlank(), "Title must be filled out")
                .check(e -> e.description() != null && !e.description().isBlank(), "Description must be filled out")
                .check(e -> e.address() != null && !e.address().isBlank(), "Address must be filled out")
                .check(e -> e.postalCode() != null && !e.postalCode().isBlank(), "Zip code must be filled out")
                .check(e -> e.city() != null && !e.city().isBlank(), "City must be filled out")
                .check(e -> e.url() != null && !e.url().isBlank(), "Url for event must be filled out")
                .check(e -> e.useDefaultImage() || (e.primaryImageUrl() != null && !e.primaryImageUrl().isBlank()), "Image must be provided unless you select default image")
                .check(e -> e.category() != null, "Category must be given")
                .check(e -> e.price() >= 0, "Price must be 0 or higher")
                .check(e -> e.startDate() != null && !e.startDate().isBefore(LocalDate.now()), "Start date must be today or in the future")
                .check(e -> e.startDate() == null || !e.startDate().isEqual(LocalDate.now()) || e.startTime() == null || !e.startTime().isBefore(LocalTime.now()), "Start time cannot be in the past")
                .get();
    }
}
