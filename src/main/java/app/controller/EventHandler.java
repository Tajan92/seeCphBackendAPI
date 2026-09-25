package app.controller;

import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.service.EventService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventHandler {
    EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    public void createEvent(Context ctx) {
        EventInputValidator(ctx);
        EventDTORequest input = EventInputValidator(ctx);
        eventService.createEvent(input);
        ctx.status(HttpStatus.CREATED);
    }

    public void getEventById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        EventDTOResponse eventDTO = eventService.getEventById(id);
        if (eventDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            return;
        }
        ctx.status(HttpStatus.OK);
        ctx.json(eventDTO);
    }

    public void getAllEvents(Context ctx) {
        List<EventDTOResponse> eventDTOS = eventService.getAllEvents();
        if (eventDTOS.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND);
        }
        ctx.status(HttpStatus.OK);
        ctx.json(eventDTOS);
    }

    public void updateEventById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        EventDTORequest input = EventInputValidator(ctx);
        eventService.updateEventById(id, input);
        ctx.status(HttpStatus.OK);
    }

    public void deleteEventById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

        boolean deleted = eventService.deleteEventById(id);
        if (!deleted) {
            ctx.status(HttpStatus.NOT_FOUND);
        }
        ctx.status(HttpStatus.OK);
    }

    private EventDTORequest EventInputValidator(Context ctx) {
        EventDTORequest input = ctx.bodyValidator(EventDTORequest.class)
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
                    return true;}, "Image must be provided unless you select default image")
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
        return input;
    }
}
