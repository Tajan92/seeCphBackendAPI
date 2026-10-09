package app.controller;

import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.enums.EventCategory;
import app.exceptions.SyncException;
import app.service.EventService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class EventHandler implements IHandler {
    EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void create(Context ctx) {
        EventDTORequest input = EventInputValidator(ctx);
        EventDTOResponse eventDTOResponse = eventService.create(input);
        if (eventDTOResponse != null) {
            ctx.status(HttpStatus.CREATED);
            ctx.json(eventDTOResponse);
        }  else {
            ctx.status(HttpStatus.BAD_REQUEST);
            log.warn("400: Invalid event request: {}", input);
        }
    }

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        EventDTOResponse eventDTO = eventService.getById(id);
        if (eventDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            log.warn("404: Event with id {} not found", id);
            return;
        }
        ctx.status(HttpStatus.OK);
        ctx.json(eventDTO);
    }

    @Override
    public void getAll(Context ctx) {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int pageSize = ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(20);

        int pageMaxSize = 100;
        if (pageSize > pageMaxSize) {
            pageSize = pageMaxSize;
        }
        if (page < 0) {
            page = 0;
        }

        String categoryParam = ctx.queryParam("category");
        String startDateParam = ctx.queryParam("startDate");
        String postalCodeParam = ctx.queryParam("postalCode");
        String searchParam = ctx.queryParam("search");

        LocalDate startDate = (startDateParam != null && !startDateParam.isBlank() ? LocalDate.parse(startDateParam) : null);
        EventCategory eventCategory = (categoryParam != null && !categoryParam.isBlank() ? EventCategory.valueOf(categoryParam.toUpperCase()) : null);

        List<EventDTOResponse> eventDTOS = eventService.getAllEventsBySearchAndFilter(searchParam, eventCategory, startDate, postalCodeParam, page, pageSize);

        ctx.status(HttpStatus.OK);
        ctx.json(eventDTOS);
    }

    public void getAllActiveEventCategories(Context ctx) {
        ctx.status(HttpStatus.OK);
        ctx.json(eventService.getAllActiveEventCategories());
    }

    @Override
    public void updateById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        EventDTORequest input = EventInputValidator(ctx);
        eventService.updateById(id, input);
        ctx.status(HttpStatus.OK);
    }

    @Override
    public void deleteById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

        boolean deleted = eventService.deleteById(id);
        if (!deleted) {
            ctx.status(HttpStatus.NOT_FOUND);
        }
        ctx.status(HttpStatus.OK);
    }

    public void syncEventsFromAPI(Context ctx) {
        String authHeader = ctx.header("x-api-key");
        String expectedSecret = System.getenv("SYNC_SECRET");

        if (expectedSecret != null && expectedSecret.equals(authHeader)) {
            CompletableFuture.runAsync(() -> {
                try {
                    eventService.syncTmEvents();
                    log.info("Background sync completed successfully.");
                } catch (Exception e) {
                    log.error("Error during background sync: {}", e.getMessage(), e);
                }
            });
            ctx.status(200).result("Ticketmaster sync started successfully.");
        } else {
            ctx.status(401).result("Unauthorized");
        }
    }

    private EventDTORequest EventInputValidator(Context ctx) {
        return ctx.bodyValidator(EventDTORequest.class)
                .check(event -> event.userId() > 0, "Invalid user id")
                .check(event -> event.title() != null && !event.title().isEmpty(), "Title must be filled out")
                .check(event -> event.description() != null && !event.description().isEmpty(), "Description must be filled out")
                .check(event -> event.address() != null && !event.address().isEmpty(), "Address must be filled out")
                .check(event -> event.postalCode() != null && !event.postalCode().isEmpty(), "Zip code must be filled out")
                .check(event -> event.city() != null && !event.city().isEmpty(), "City must be filled out")
                .check(event -> event.url() != null && !event.url().isEmpty(), "Url for event must be filled out")
                .check(dto -> {
                    if (!dto.useDefaultImage()) {
                        return dto.primaryImageUrl() != null && !dto.primaryImageUrl().isEmpty();
                    }
                    return true;
                }, "Image must be provided unless you select default image")
                .check(event -> event.category() != null && !event.category().toString().isEmpty(), "Category must be given")
                .check(event -> !(event.price() < 0), "Price must be 0 or higher")
                .check(event -> event.startDate() != null, "Start date must be filled out")
                .check(event -> !event.startDate().isBefore(LocalDate.now()), "Start date cannot be in the past")
                .check(event -> {
                    if (event.startDate() != null && event.startDate().isEqual(LocalDate.now())) {
                        return event.startTime() == null || !event.startTime().isBefore(LocalTime.now());
                    }
                    return true;
                }, "Start time cannot be in the past").get();
    }
}
