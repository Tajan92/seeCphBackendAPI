package app.controller;

import app.dao.EventDAO;
import app.dto.event.EventDTO;
import app.dto.event.EventDTORequest;
import app.entities.Event;
import app.service.EventService;

import io.javalin.http.Context;

import java.util.List;

public class EventHandler {
    EventService eventService;

    public EventHandler(EventDAO eventDAO) {
        this.eventService = new EventService(eventDAO);
    }

    public void createEvent(Context ctx) {
        EventDTORequest input = ctx.bodyAsClass(EventDTORequest.class);
        eventService.createEvent(input);
        ctx.status(201);
    }

    public void getEventById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        EventDTO eventDTO = eventService.getEventById(id);
        ctx.status(200);
        ctx.json(eventDTO);
    }

    public void getAllEvents(Context ctx) {
        List<EventDTO> eventDTOS = eventService.getAllEvents();
        ctx.status(200);
        ctx.json(eventDTOS);
    }

    public void updateEventById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        EventDTORequest input = ctx.bodyAsClass(EventDTORequest.class);
        eventService.updateEventById(id, input);
        ctx.status(201);
    }

    public void deleteEventById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        eventService.deleteEventById();
        ctx.status(200);
    }
}
